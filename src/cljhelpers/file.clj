(ns cljhelpers.file
  (:require [clojure.java.io :as io])
  (:import (java.io FileReader PushbackReader FileInputStream BufferedWriter FileWriter BufferedReader)
           (javas FileUtil)))

(defn get-app-path [] (str (-> (java.io.File. "") .getCanonicalPath)))

(defn get-file-path
  "Get the filename"
  [file]
  (.getAbsolutePath file)
  )

(defn get-file-name
  "Get the filename"
  [file]
  (.getName file)
  )

(defn get-file-name-wo-extension
  "Get the filename"
  [file]
  (let [file-name (.getName file)
        pos (.lastIndexOf file-name ".")]
    (cond (> pos 0) (.substring file-name 0 pos) :else file-name)
    )
  )

(defn getDirectoryFiles
  [dir-path]
  (file-seq (io/file dir-path)))

(defn getDesktopDirectory
  []
  (let [dir (str (System/getProperty "user.Home") "/Desktop")]
    dir
    )
  )

(defn read-edn-file
  "Returns evaluated content of edn file"
  [filename]
  (with-open [r (PushbackReader. (FileReader. filename))]
    (read r)))

(defn readEdnFile
  [file-path]
  (let [data (slurp file-path)] (read-string data))
  )

(defn save-edn-file
  [filename]
  )

(defn get-file-input-stream
  [fInputStream]
  (FileUtil/readFully fInputStream -1 true)
  )

(defn get-file-binary
  [file-path]
  (FileUtil/readFully (FileInputStream. file-path) -1 true)
  )

(defn get-file-reader
  [file-path]
  (let [fr (FileReader. file-path)
        br (BufferedReader. fr)]
    br
    )
  )

(defn- append-to-file [fp line]
  (let [fw (FileWriter. fp true)
        out (BufferedWriter. fw)
        _ (.write out (str line (System/getProperty "line.separator")) )
        ]
    (.close out)
    )
  )

(defn write-to-file [fp line]
  (let [fw (FileWriter. fp false)
        out (BufferedWriter. fw)
        _ (.write out (str line (System/getProperty "line.separator")) )
        ]
    (.close out)
    )
  )

(defn update-file [fp l]
  (let [_fr (get-file-reader fp) ]
    (loop [line (-> _fr .readLine) lf false]
      (if (and (false? lf) (-> line nil? not) )
        (let [lf? (= line l)]
          (recur (-> _fr .readLine) lf?)
          )
        (if (false? lf) (append-to-file fp l))
        )
      )
    )
  )

