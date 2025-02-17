(ns cljhelpers.plugin
  (:import (java.io File)
           (java.net URLClassLoader))
  )

(defn is-in-cp [sfile urls]
  (def found false)
  (loop [i 0]
    (if (< i (count urls))
      (do (if (= sfile (.getFile (nth urls i))) (def found true))
          (recur (inc i))
          )
      )
    )
  (= found true)
  )

(defn add-to-classpath [s]
  (def url (.. (File. s) toURI toURL))
  (let [urlClassLoader (-> (Thread/currentThread) (.getContextClassLoader))]
    (def urls (.getURLs urlClassLoader))
    (when (= (is-in-cp (.getFile url) urls) false)
      (def params (into-array Class [(.getClass url)]) )
      (def urlClass (.getClass (URLClassLoader. urls)))
      (let [method (.getDeclaredMethod urlClass "addURL" params)]
        (.setAccessible method true)
        (.invoke method urlClassLoader (object-array [url]))
        )
      )
    )
  )

