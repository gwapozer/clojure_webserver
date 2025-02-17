(ns pointsensor-dashboard.utils.gui
  (:require [compojure.core :refer [defroutes]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.java.io :as io]
            [cljhelpers.fx_obj :as fo]
            [pointsensor-dashboard.state :refer [app-state app-states dispatch handle-event set-get-atom-app-state]]
            [clojure.core.async :refer [>!! timeout close! alts! chan go go-loop <! >!]]
            [pointsensor_dashboard.widget.prompt :refer :all]
            [cljhelpers.widget :refer :all]
            [ring.adapter.jetty :as ring]
            [pointsensor_dashboard.views.layout :as layout]
            [compojure.route :as route]
            [pointsensor_dashboard.views.index :as index-model]
            [pointsensor-dashboard.tasks.schedule-sensor :refer :all]
            [pointsensor-dashboard.tasks.schedule-report :refer :all]
            [pointsensor-dashboard.tasks.schedule-sensor :refer :all]
            [pointsensor-dashboard.tasks.schedule-db :refer :all]
            [cljhelpers.scheduler :refer :all]
            [pointsensor_dashboard.controllers.psdb_controller :refer :all]
            [cljhelpers.objEval :as oe])
  (:import (javafx.scene SceneBuilder)
           (javafx.stage StageBuilder)
           (javafx.fxml FXMLLoader)
           (javafx.application Platform)
           (javafx.event EventHandler)
           (java.awt.event ActionEvent)
           (javafx.collections FXCollections)
           (javas ProcessUtil))
  (:use cljhelpers.FxRun)
  )

(def atom-scheduler (atom []))

(defn set-get-atom-scheduler
  ([] @atom-scheduler)
  ([x] (reset! atom-scheduler x))
  )

(defn stop-all-scheduler []
  (let [curr-vect (set-get-atom-scheduler)]
    (loop [i 0]
      (if (< i (count curr-vect))
        (let [s (nth curr-vect i)]
          (.shutdownNow s)
          (recur (inc i))
          )
        )
      )
    (set-get-atom-scheduler [])
    )
  )

(defn start-scheduler
  []
  (let [
        _ (stop-all-scheduler)
        _ms1 (my-schedule 1 1 60 (run-weekly-sensor-schedule-report))
        _ms2 (my-schedule 1 1 3 (run-process-sensor-event))
        _ms3 (my-schedule 1 1 5 (run-process-sensor-temp))
        _ms4 (my-schedule 1 2 10 (run-process-email-for-sensor-event))
        _ms5 (my-schedule 1 2 1440 (run-process-db-backup-event))

        _ (set-get-atom-scheduler (conj (set-get-atom-scheduler) _ms1))
        _ (set-get-atom-scheduler (conj (set-get-atom-scheduler) _ms2))
        _ (set-get-atom-scheduler (conj (set-get-atom-scheduler) _ms3))
        _ (set-get-atom-scheduler (conj (set-get-atom-scheduler) _ms4))
        _ (set-get-atom-scheduler (conj (set-get-atom-scheduler) _ms5))
        ]
    )
  )

(defroutes routes
           index-model/routes
           (route/resources "/")
           (route/not-found (layout/common-layout "point sensor"
                                                  (layout/not-found)
                                                  [:div {:class "clear"}])))

(def application (wrap-defaults routes site-defaults))

(def my-server (ring/run-jetty application {:port 80
                                            :join? false}))
(.stop my-server)

(defn start [port]
  (.start my-server)
  ;(ring/run-jetty application {:port  port
  ;                             :join? false})
  )

(def atom-main-stage (atom nil))

(defn set-get-atom-main-stage
  ([] @atom-main-stage)
  ([x] (reset! atom-main-stage x))
  )

(def loc (clojure.java.io/resource "Main.fxml"))
(def loader  (FXMLLoader/load loc))

(defn- load-loginfxml []
  (def login-obj (FXMLLoader/load (io/resource "server.fxml")))
  (def xml-obj (fo/fxnode (set-get-atom-main-stage) :loginPANE))
  (run-now (.setContent xml-obj login-obj))
  )

(defn set-on-close
  "Forces exit when the main window is closed"
  [stage]
  (.setOnCloseRequest stage (reify EventHandler
                              (handle [this event]
                                (do (.stop my-server) (stop-all-scheduler) (Platform/exit) )
                                )
                              ))
  )

(defn- close-fx-pane
  "Hide all panes"
  []
  (let [accordion (fo/fxnode (set-get-atom-main-stage) :acdLogin)]
    (def panes (.getPanes accordion))
    (if-not (nil? panes)
      (map #(.setVisible % false) panes)
      )
    )
  )

(defn- expand-fx-pane
  "Sets the javafx pane to visible if necessary and expand it"
  [kw]
  (close-fx-pane)
  (let [node (fo/fxnode (set-get-atom-main-stage) kw)
        accordion (fo/fxnode (set-get-atom-main-stage) :acdLogin)]
    (if-not (.isVisible node)
      (.setVisible node true)
      )
    (if-not (.isExpanded node)
      (.setExpandedPane accordion node)))
  )

(defn- expand-fx-login-pane
  "Sets the javafx pane to visible if necessary and expand it"
  [kw]
  (let [node (fo/fxnode (set-get-atom-main-stage) kw)
        accordion (fo/fxnode (set-get-atom-main-stage) :acdLogin)]
    (if-not (.isVisible node)
      (.setVisible node true)
      )
    (if-not (.isExpanded node)
      (.setExpandedPane accordion node)))
  )

(defn- set-selectedtab [tabpane-id tab-id pane-id]
  (let [tabpaneobj (.getSelectionModel (fo/fxnode (set-get-atom-main-stage) tabpane-id) )
        tabobj (.getTabs (fo/fxnode (set-get-atom-main-stage) tabpane-id) )]
    (loop [i 0]
      (if (< i (.size tabobj))
        (do
          (def my-tab (.get tabobj i))
          (if (= (.getId (fo/fxnode (set-get-atom-main-stage) tab-id)) (.getId my-tab))
            (do
              (.select tabpaneobj my-tab)
              (expand-fx-pane pane-id)
              )
            )
          (recur (inc i))
          )
        )
      )
    )
  )

(defn- sortaccd [accdpaneobj selected-pane-id]

  (def buffdata (FXCollections/observableArrayList accdpaneobj))
  (def id  (str(read-string (reduce str (rest (prn-str selected-pane-id)))) ))

  ;TODO sort observable list to make code look more elegant
  (def testdata (FXCollections/observableArrayList accdpaneobj))

  (doto accdpaneobj
    (.removeAll accdpaneobj)
    )

  (loop [i 0]
    (if (< i (.size buffdata))
      (do
        (def my-titlepane (.get buffdata i))

        (if (= id (.getId my-titlepane))
          (doto accdpaneobj
            (.add my-titlepane)
            )
          )
        (recur (inc i))
        )
      )
    )

  (loop [i 0]
    (if (< i (.size buffdata))
      (do
        (def my-titlepane (.get buffdata i))

        (if-not (= id (.getId my-titlepane))
          (doto accdpaneobj
            (.add my-titlepane)
            )
          )
        (recur (inc i))
        )
      )
    )
  )

;GS added: should display only one accordion at time
(defn- set-selectedaccordion [accd-id selected-pane-id]
  (def accdpaneobj (.getPanes (fo/fxnode (set-get-atom-main-stage) accd-id)))
  (sortaccd accdpaneobj selected-pane-id)

  (loop [i 0]
    (if (< i (.size accdpaneobj))
      (do
        (def my-titlepane (.get accdpaneobj i))

        (if-not (= (.getId (fo/fxnode (set-get-atom-main-stage) selected-pane-id)) (.getId my-titlepane))
          (.setVisible my-titlepane false)
          )
        (recur (inc i))
        )
      )
    )
  )
;END

(defn set-init
  "Initialize GUI display (Automate later if more tabs are added)"
  []
  (set-selectedaccordion :acdLogin :loginPANE)
  (set-selectedtab :tpMain "tbLogin" :loginPANE)

  (expand-fx-login-pane :loginPANE)
  )

(defmethod handle-event :logged
  [state]
  (-> state
      (assoc-in [:logged] true)
      )
  )

(defmethod handle-event :logged-out
  [state]
  (-> state
      (assoc-in [:logged] false)
      )
  )

(defn logout-user []
  (do
    (let [_app-state (set-get-atom-app-state)
          _asmod (-> _app-state (assoc :logged false :user nil))
          _ (set-get-atom-app-state _asmod)])

    (.setText (fo/fxnode (set-get-atom-main-stage) :username) "")
    (.setText (fo/fxnode (set-get-atom-main-stage) :password) "")

    (.setText (fo/fxnode (set-get-atom-main-stage) :hlLogin) "Login")
    (.setText (fo/fxnode (set-get-atom-main-stage) :loginBTN) "Login")

    (set-init)

    ;(set-selectedaccordion :acdLogin :loginPANE)
    ;(set-selectedtab :tpMain "tbLogin" :loginPANE)
    )
  )
(defn logout
  "Logout event"
  []
  (let [c (:operations-channel @app-state)]
    (go (>! c :end))
    (logout-user)
    )
  )

(defn session-expiration
  "Displays the session expiring dialog after expiration milliseconds post the last data on the in channel"
  [in allotted-mins warning-mins ]
  ;(println "In session-expiration function")
  (let [dialog-chan (chan 1)]
    (go-loop [counter 0
              dialog-counter 0]
             (let [
                   [val _] (alts! [in (timeout (* 1000 60 allotted-mins))])
                   ]
               (if val
                 (if-not (= val :end)
                   (recur (inc counter) dialog-counter))
                 (do
                   (async-dialog (set-get-atom-main-stage) dialog-chan warning-mins)
                   (let [
                         dialog (<! dialog-chan)
                         [ret _] (alts! [dialog-chan (timeout (* warning-mins 60 1000))])
                         ]
                     (cond
                       (= ret false)
                       (do
                         (close! in)
                         (run-now (.close dialog))
                         (close! dialog-chan)
                         ;(logout)
                         (run-now (logout))
                         ;(run-now (set-selectedtab :tpMain "tbLogin" :loginPANE))
                         )
                       (= ret true)
                       (do
                         (trampoline session-expiration in allotted-mins warning-mins)
                         )
                       :else
                       (do
                         (close! in)
                         (run-now (.close dialog))
                         (close! dialog-chan)
                         (run-now (logout))
                         ;(run-now (logout))
                         ;(run-now (set-selectedtab :tpMain "tbLogin" :loginPANE))
                         )
                       )
                     ))
                 )
               )
             )))

(defn start-server []
  (println "Start server")
  (.start my-server)
  (start-scheduler)
  (.setText (fo/fxnode (set-get-atom-main-stage) :lblServerStatus) "Server Started")
  )

(defn stop-server []
  (println "Stop server")
  (.stop my-server)
  (stop-all-scheduler)
  (.setText (fo/fxnode (set-get-atom-main-stage) :lblServerStatus) "Server Stopped")
  )

(defn login
  "Login event"
  ([]
   (if (false? (:logged @app-state))
     (let [username (-> (fo/fxnode (set-get-atom-main-stage) :username) .getText)
           password (-> (fo/fxnode (set-get-atom-main-stage) :password) .getText)
           -user nil
           ]
       (if (-> -user nil? not)
         (do
           (dispatch {:event :logged})
           (let [
                 ch (chan 1)
                 _app-state (set-get-atom-app-state)
                 _asmod (-> _app-state (assoc :logged true :user -user :operations-channel ch))
                 _ (set-get-atom-app-state _asmod)]

             (session-expiration ch 15 5)

             (.setText (fo/fxnode (set-get-atom-main-stage) :hlLogin) "Log out")
             (.setText (fo/fxnode (set-get-atom-main-stage) :loginBTN) "Log out")
             )
           )
         (do
           (alert-message "Login error" "Username or password is invalid.")
           )
         )
       )
     (logout)
     )
    )
  ([hlLink]
   (if (:logged @app-state)
     (logout)
     )
   (set-init)
    )
  )

(def my-stage
  (run-now
    (let [stage (.. StageBuilder create
                    (title "Point Sensor Dashboard")
                    (scene (.. SceneBuilder create
                               (root loader)
                               build))
                    build)
          _ (-> stage (.setResizable false))]
      stage
      )
    )
  )

;(defn watch-it "Adds watch on the app-state atom" []
;  (add-watch app-state :watcher
;             (fn [key atom old-state new-state]
;               (swap! app-states conj @app-state)
;               (let [updates (first (data/diff new-state old-state))]
;                 (when-some [logged (-> updates :logged)]
;                   (if logged
;                     (do
;                       (.setText (fo/fxnode (set-get-atom-main-stage) :hlLogin) "Log Out")
;                       )
;                     (do
;                       (.setText (fo/fxnode (set-get-atom-main-stage) :hlLogin) "Login")
;                       )
;                     )
;                   )
;                 ))
;             )
;  )

(defn- enliven
  "Build GUI event handlers"
  []
  ;(-> (fo/fxnode (set-get-atom-main-stage) :loginBTN) (.setOnAction (proxy [EventHandler] [] (handle [^ActionEvent event] (login)))))
  ;(-> (fo/fxnode (set-get-atom-main-stage) :hlLogin) (.setOnAction (proxy [EventHandler] [] (handle [^ActionEvent event] (login :hlLogin)))))
  (-> (fo/fxnode (set-get-atom-main-stage) :startBTN) (.setOnAction (proxy [EventHandler] [] (handle [^ActionEvent event] (start-server)))))
  (-> (fo/fxnode (set-get-atom-main-stage) :stopBTN) (.setOnAction (proxy [EventHandler] [] (handle [^ActionEvent event] (stop-server)))))
  ;(-> (fo/fxnode (set-get-atom-main-stage) :btnTest) (.setOnAction (proxy [EventHandler] [] (handle [^ActionEvent event] (do (println "Test") (test-sensor-list))))))
  )

(defn init []
  (set-get-atom-main-stage my-stage)
  (run-now (.show (set-get-atom-main-stage)))
  (load-loginfxml)
  (set-on-close (set-get-atom-main-stage))
  (set-init)
  ;;(watch-it)
  (enliven)

  (let [port (Integer. (or (System/getenv "PORT") "80"))
        ;_ (init)
        ;_ (oe/eval-obj 'pointsensor_dashboard.core [(println "Point Sensor started")] "Point sensor start") (oe/load-gui-tracer)
        ]
    ;(start port)
    )
  )