(ns cljhelpers.FxRun
  (:require
    [clojure.core.async :refer [go put! chan <!]])
  )

(defonce force-toolkit-init (javafx.embed.swing.JFXPanel.))

(defn run-later*
  [f]
  (javafx.application.Platform/runLater f))

(defmacro run-later
  [& body]
  `(run-later* (fn [] ~@body)))

(defn run-now*
  [f]
  (let [result (promise)]
    (run-later
      (deliver result (try (f) (catch Throwable e e))))
    @result))

(defmacro run-now
  [& body]
  `(run-now* (fn [] ~@body)))

(defn event-handler*
  [f]
  (reify javafx.event.EventHandler
    (handle [this e] (f e))))

(defmacro event-handler [arg & body]
  `(event-handler* (fn ~arg ~@body)))


(defn async-run-wrapper [f ch]
  (fn []
    (let [res
          (try
            (f)
            (catch Throwable ex
              (put! ch (ex-info (str "Error on JavaFX application thread") {:cause ex} ex))
              (throw ex)                                    ;; Should this be rethrown??
              ))]
      (put! ch (if (nil? res) ::nil res)))))

(defn process-async-res [res]
  (cond
    (instance? Throwable res)
    (throw res)

    (= ::nil res)
    nil

    :default
    res))

(defn- run<* [take-fn body]
  `(if (javafx.application.Platform/isFxApplicationThread)
     (do ~@body)
     (let [ch# (clojure.core.async/chan)]
       (javafx.application.Platform/runLater
         (async-run-wrapper (fn [] ~@body) ch#))
       (process-async-res (~take-fn ch#)))))


(defmacro run<!
  "Runs the enclosed body asynchronously on the JavaFX application thread
  from within a core.async go block. Returns the value of the evaluated body
  using a core.async chan and the <! function. Must be called from within a
  core.async go block!"
  [& body]
  (run<* 'clojure.core.async/<! body))

(defmacro run<!!
  "Runs the enclosed body asynchronously on the JavaFX application thread.
  Blocks the calling thread until asynchronous execution is complete and
  returns the result of the evaluated block to the caller."
  [& body]
  (run<* 'clojure.core.async/<!! body))