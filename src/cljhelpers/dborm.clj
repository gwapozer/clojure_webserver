(ns cljhelpers.dborm
  (:require [clojure.java.jdbc :as jdbc]
            [cljhelpers.fx_obj :as fx_obj]
            [pointsensor_dashboard.enums.enumerator :as enumobj]
            [cljhelpers.objEval :as oe])
  (:import (java.util Date)
           (java.sql Time Blob Clob Timestamp Types Statement)))

(defrecord Param-Cl [id value in-outType])
(defrecord Where-Cl [field filter value condition Where-Cl])
(defrecord Limit-Cl [value offset])
(defrecord obj-val [data-type value])

(defn- get-datatype [value]
  (cond
    (nil? value) nil
    (instance? Boolean value) "BOOLEAN"
    (instance? Byte value) "BYTE"
    (instance? Short value) "SHORT"
    (instance? Integer value) "INTEGER"
    (instance? Long value) "LONG"
    (instance? Float value) "FLOAT"
    (instance? Double value) "DOUBLE"
    (instance? BigDecimal value) "DECIMAL"
    (instance? String value) "STRING"
    (instance? Date value) "DATE"
    (instance? Time value) "TIME"
    (instance? Timestamp value) "TIMESTAMP"
    (instance? Blob value) "BLOB"
    (instance? Clob value) "CLOB"
    (instance? (byte []) value) "BINARY"
    :else "OBJECT"
    )
  )

(defn- set-data [prep-stmt set-cnt value]
  (cond
    (nil? value) (doto prep-stmt (.setObject set-cnt value))
    (instance? Boolean value) (doto prep-stmt (.setBoolean set-cnt value))
    (instance? Byte value) (doto prep-stmt (.setByte set-cnt value))
    (instance? Short value) (doto prep-stmt (.setShort set-cnt value))
    (instance? Integer value) (doto prep-stmt (.setInt set-cnt value))
    (instance? Long value) (doto prep-stmt (.setLong set-cnt value))
    (instance? Float value) (doto prep-stmt (.setFloat set-cnt value))
    (instance? Double value) (doto prep-stmt (.setDouble set-cnt value))
    (instance? BigDecimal value) (doto prep-stmt (.setBigDecimal set-cnt value))
    (instance? String value) (doto prep-stmt (.setString set-cnt value))
    (instance? Date value) (doto prep-stmt (.setObject set-cnt value))
    (instance? Time value) (doto prep-stmt (.setObject set-cnt value))
    (instance? Timestamp value) (doto prep-stmt (.setTimeStamp set-cnt value))
    (instance? Blob value) (doto prep-stmt (.setBlob set-cnt value))
    (instance? Clob value) (doto prep-stmt (.setClob set-cnt value))
    (instance? (byte []) value) (doto prep-stmt (.setBytes set-cnt value))
    :else prep-stmt
    )
  )

(defn- set-param-data [prep-stmt name value]
  (cond
    (nil? value) (doto prep-stmt (.setObject name value))
    (instance? Boolean value) (doto prep-stmt (.setBoolean name value))
    (instance? Byte value) (doto prep-stmt (.setByte name value))
    (instance? Short value) (doto prep-stmt (.setShort name value))
    (instance? Integer value) (doto prep-stmt (.setInt name value))
    (instance? Long value) (doto prep-stmt (.setLong name value))
    (instance? Float value) (doto prep-stmt (.setFloat name value))
    (instance? Double value) (doto prep-stmt (.setDouble name value))
    (instance? BigDecimal value) (doto prep-stmt (.setBigDecimal name value))
    (instance? String value) (doto prep-stmt (.setString name value))
    (instance? Date value) (doto prep-stmt (.setObject name value))
    (instance? Time value) (doto prep-stmt (.setObject name value))
    (instance? Timestamp value) (doto prep-stmt (.setTimeStamp name value))
    (instance? Blob value) (doto prep-stmt (.setBlob name value))
    (instance? Clob value) (doto prep-stmt (.setClob name value))
    (instance? (byte []) value) (doto prep-stmt (.setBytes name value))
    :else (println "test gs")
    )
  )

(defn- set-data-out [prep-stmt set-cnt type]
  (doto prep-stmt (.registerOutParameter set-cnt type))
  )

(defn- get-data [data-type result i]
  (cond
    (or (= data-type (Types/BIT)) (= data-type "BIT"))  (.getBoolean result i)
    (or (= data-type (Types/TINYINT)) (= data-type "TINYINT")) (.getShort result i)
    (or (= data-type (Types/SMALLINT)) (= data-type "SMALLINT")) (.getShort result i)
    (or (= data-type (Types/INTEGER)) (= data-type "INTEGER") (= data-type "INT")) (.getInt result i)
    (or (= data-type (Types/BIGINT)) (= data-type "BIGINT")) (.getLong result i)
    (or (= data-type (Types/FLOAT)) (= data-type "FLOAT")) (.getFloat result i)
    (or (= data-type (Types/REAL)) (= data-type "REAL")) (.getFloat result i)
    (or (= data-type (Types/DOUBLE)) (= data-type "DOUBLE")) (.getDouble result i)
    (or (= data-type (Types/NUMERIC)) (= data-type "NUMERIC")) (.getBigDecimal result i)
    (or (= data-type (Types/DECIMAL)) (= data-type "DECIMAL")) (.getBigDecimal result i)
    (or (= data-type (Types/CHAR)) (= data-type "CHAR")) (.getString result i)
    (or (= data-type (Types/VARCHAR)) (= data-type "VARCHAR")) (.getString result i)
    (or (= data-type (Types/LONGVARCHAR)) (= data-type "LONGVARCHAR")) (.getString result i)
    (or (= data-type (Types/DATE)) (= data-type "DATE")) (.getDate result i)
    (or (= data-type (Types/TIME)) (= data-type "TIME")) (.getTime result i)
    (or (= data-type (Types/TIMESTAMP)) (= data-type "TIMESTAMP")) (.getObject result i)
    (or (= data-type (Types/BINARY)) (= data-type "BINARY")) (.getBytes result i)
    (or (= data-type (Types/VARBINARY)) (= data-type "VARBINARY")) (.getBytes result i)
    (or (= data-type (Types/LONGVARBINARY)) (= data-type "LONGVARBINARY")) (.getBytes result i)
    (or (= data-type (Types/NULL)) (= data-type "NULL")) (.getObject result i)
    (or (= data-type (Types/OTHER)) (= data-type "OTHER")) (.getObject result i)
    (or (= data-type (Types/JAVA_OBJECT)) (= data-type "JAVA_OBJECT")) (.getObject result i)
    (or (= data-type (Types/DISTINCT)) (= data-type "DISTINCT")) (.getObject result i)
    (or (= data-type (Types/STRUCT)) (= data-type "STRUCT")) (.getObject result i)
    (or (= data-type (Types/ARRAY)) (= data-type "ARRAY")) (.getObject result i)
    (or (= data-type (Types/BLOB)) (= data-type "BLOB")) (.getBlob result i)
    (or (= data-type (Types/CLOB)) (= data-type "CLOB")) (.getClob result i)
    (or (= data-type (Types/REF)) (= data-type "REF")) (.getRef result i)
    (or (= data-type (Types/DATALINK)) (= data-type "DATALINK")) (.getObject result i)
    (or (= data-type (Types/BOOLEAN)) (= data-type "BOOLEAN")) (.getBoolean result i)
    (or (= data-type (Types/ROWID)) (= data-type "ROWID")) (.getRowId result i)
    (or (= data-type (Types/NCHAR)) (= data-type "NCHAR")) (.getString result i)
    (or (= data-type (Types/NVARCHAR)) (= data-type "NVARCHAR")) (.getString result i)
    (or (= data-type (Types/LONGNVARCHAR)) (= data-type "LONGNVARCHAR")) (.getString result i)
    (or (= data-type (Types/NCLOB)) (= data-type "NCLOB")) (.getNClob result i)
    (or (= data-type (Types/SQLXML)) (= data-type "SQLXML")) (.getSQLXML result i)
    (or (= data-type (Types/REF_CURSOR)) (= data-type "REF_CURSOR")) (.getObject result i)
    (or (= data-type (Types/TIME_WITH_TIMEZONE)) (= data-type "TIME_WITH_TIMEZONE")) (.getObject result i)
    (or (= data-type (Types/TIMESTAMP_WITH_TIMEZONE)) (= data-type "TIMESTAMP_WITH_TIMEZONE")) (.getObject result i)
    :else (.getObject result i)
    )
  )

(defn- get-string-to-delim [strgs delim]
  (loop [i 0 my-string ""]
    (if (not= (str (nth strgs i)) delim)
      (do
        (recur (inc i) (str my-string (nth strgs i)))
        )
      (do
        (str my-string)
        )
      )
    )
  )

(defn- get-record-name
  [record]
  (def str-data (prn-str record))
  (def str-mod (get-string-to-delim str-data "{"))
  (def str-last (get-string-to-delim (reduce str (reverse str-mod))  "."))
  (reduce str (reverse str-last))
  )

(defn- get-record-keys-list
  ([entity]
   (let [rec-list (map #(reduce str (rest (str %)) )(keys entity))]
     rec-list)
    )
  ([entity-definition entity]
   (let [pks (enumobj/GetEnumListByKey entity-definition :is-pk true)
         pks-value (enumobj/GetEnumValueList pks :name)
         record-keys (get-record-keys-list entity)]
     (remove (into #{} pks-value) record-keys)
     )
    )
  )

(defn- get-record-values-list
  ([entity]
   (let [record-keys (get-record-keys-list entity)
         new-keys (map #(-> entity (read-string (str ":" %))) record-keys)]
     (zipmap new-keys (map #(-> entity %) new-keys))
     )
    )
  ([entity-definition entity]
   (let [record-keys (get-record-keys-list entity-definition entity )
         new-keys (map #(-> entity (read-string (str ":" %))) record-keys)]
     (zipmap new-keys (map #(-> entity %) new-keys))
     )
    )
  )

(defn- get-record-values-objdata-list
  ([val-list]
   (def seq-val (seq val-list))
   (loop [i 0 my-vec []]
     (if (< i (count seq-val))
       (let [my-val (nth seq-val i)
             data-val (val my-val)
             data-type (get-datatype data-val)]
         (def new-objval (obj-val. data-type data-val))
         (recur (inc i) (conj my-vec new-objval))
         )
       my-vec
       )
     )
    )
  ([entity-definition val-list]
   (def seq-val (seq val-list))
   (loop [i 0 my-vec []]
     (if (< i (count seq-val))
       (let [my-val (nth seq-val i)
             key-val (key my-val)
             data-val (val my-val)
             data-type (-> (enumobj/GetEnumByKey entity-definition :name (fx_obj/to-id key-val)) :data-type)]
         (def new-objval (obj-val. data-type data-val))
         (recur (inc i) (conj my-vec new-objval))
         )
       my-vec
       )
     )
    )
  )

(defn- get-record-values
  ([entity]
   (let [val-list (get-record-values-list entity)]
     (get-record-values-objdata-list val-list))
    )
  ([entity-definition entity]
   (let [val-list (get-record-values-list entity-definition entity)]
     (get-record-values-objdata-list entity-definition val-list))
    )
  )

(defn- get-record-keys-cl
  ([record]
   (let [key-list (get-record-keys-list record)]
     (loop [i 0 my-string ""]
       (if (< i (count key-list))
         (do
           (recur (inc i) (str my-string (cond (= i 0) "" :else ", ") (nth key-list i) ))
           )
         (do
           (str my-string)
           )
         )
       )))
  ([entity-definition record]
   (let [key-list (get-record-keys-list entity-definition record)]
     (loop [i 0 my-string ""]
       (if (< i (count key-list))
         (do
           (recur (inc i) (str my-string (cond (= i 0) "" :else ", ") (nth key-list i) ))
           )
         (do
           (str my-string)
           )
         )
       )))
  )

(defn- in-outType?
  [parameters]
  (false? (every? #(nil? %) (map #(:in-outType %) parameters)))
  )

(defn- insert-cl-mapper [record-keys]
  (let [cl-mapper (apply str (butlast (apply str (repeatedly (count record-keys) #(str "?,")))))]
    cl-mapper)
  )

(defn- insert-cl
  ([entity]
   (let [record-keys (get-record-keys-list entity)]
     (insert-cl-mapper record-keys)
     )
    )
  ([entity-definition entity]
   (let [record-keys (get-record-keys-list entity-definition entity)]
     (insert-cl-mapper record-keys)
     ))
  )

(defn- update-cl-mapper [record-keys]
  (let [cl-mapper (apply str (butlast (apply str (map #(str % "= ?,") record-keys))))]
    cl-mapper)
  )

(defn- update-cl
  ([entity]
   (let [record-keys (get-record-keys-list entity)]
     (update-cl-mapper record-keys)
     ))
  ([entity-definition entity]
   (let [record-keys (get-record-keys-list entity-definition entity)]
     (update-cl-mapper record-keys)
     ))
  )

(defn- where-cl [clauses]
  (loop [i 0 sql-str ""]
    (if (< i (count clauses))
      (do
        (def clause (nth clauses i))
        (def sql
          (str
            (cond (nil? (:field clause)) "" :else (str " " (:field clause) " " (:filter clause) " " "?"))
            (cond (nil? (:condition clause)) "" :else (str " " (:condition clause) " "))
            (cond (nil? (:Where-Cl clause)) "" :else (str " (" (where-cl (:Where-Cl clause)) ") "))))
        (recur (inc i) (str sql-str sql))
        )
      sql-str
      )
    )
  )

(defn- limit-cl [clause]
  (let [sql-str (str " limit " (:offset clause) "," (:value clause))]
    sql-str
    )
  )

(defn- proc-cl [parameters]
  (let [cl-mapper (apply str (butlast (apply str (map (fn[_](str "?,"))  parameters))))]
    cl-mapper)
  )

(defn- create-prep-stmnt
  ([prep-stmt x clauses]
   (loop [i 0 set-cnt x]
     (if (< i (count clauses))
       (do
         (def clause (nth clauses i))
         (set-data prep-stmt set-cnt (:value clause))
         (if-not (nil? (:Where-Cl clause))
           (create-prep-stmnt prep-stmt (+ set-cnt 1) (:Where-Cl clause))
           )
         (recur (inc i) (inc set-cnt))
         )
       prep-stmt
       )
     )
    )
  )

(defn- prep-statement
  ([prep-stmt clauses]
   (create-prep-stmnt prep-stmt 1 clauses))
  ([prep-stmt entity-values clauses]
   (let [prep-entity-vals (create-prep-stmnt prep-stmt 1 entity-values)]
     (create-prep-stmnt prep-entity-vals (inc (count entity-values)) clauses))
    )
  )

(defn- create-proc-prep-stmnt
  ([prep-stmt x parameters]
   (loop [i 0 set-cnt x]
     (if (< i (count parameters))
       (let [parameter (nth parameters i)]
         (cond (nil? (:in-outType parameter))
               (set-param-data prep-stmt (:id parameter) (:value parameter))
               :else
               (set-data-out prep-stmt set-cnt (:in-outType parameter))
               )
         (recur (inc i) (inc set-cnt))
         )
       prep-stmt
       )
     )
    )
  )

(defn- proc-prep-statement
  ([prep-stmt parameters]
   (create-proc-prep-stmnt prep-stmt 1 parameters))
  ([prep-stmt entity-values parameters]
   (let [prep-entity-vals (create-proc-prep-stmnt prep-stmt 1 entity-values)]
     (create-proc-prep-stmnt prep-entity-vals (inc (count entity-values)) parameters))
    )
  )

(defn- make-hash
  "Return data in hash-map"
  [results]
  (loop [i 1 hash-buffer {}]
    (if (<= i (-> results (.getMetaData) (.getColumnCount)))
      (let [col-name (-> results (.getMetaData) (.getColumnName i))
            col-type (-> results (.getMetaData) (.getColumnTypeName i))]
        (def hash-data {(read-string (str ":" (.toLowerCase col-name)))
                        (get-data col-type results i)})
        (recur (inc i) (conj hash-buffer hash-data))
        )
      (do
        (conj hash-buffer hash-data)
        )
      )
    )
  )

(defn- make-entity
  "Return entity from hash-map"
  [entity-name hash-result]
  (let [entity-map (read-string (str "map->" entity-name))]
    ((eval entity-map) hash-result)
    )
  )

(defn- map-data
  [results]
  (cond (= (nil? results) false)
        (loop [i 0 rs-rlt (.next results) vec-buffer []]
          (if (< i (.getRow results))
            (do
              (def rslt (make-hash results))
              (recur (inc i) (def rs-rlt (.next results)) (conj vec-buffer rslt))
              )
            vec-buffer
            ))
        :else [])
  )

(defn- map-in-outType
  [sql-ps parameters]
  (loop [i 0 vec-buffer []]
    (if (< i (count parameters))
      (let [parameter (nth parameters i)
            hash-data (cond (not= (:in-outType parameter) nil)
                            (let [hash {(read-string (str ":" (.toLowerCase (:id parameter))))
                                        (get-data (:in-outType parameter) sql-ps (+ i 1))}]
                              hash
                              )
                            :else nil
                            )]
        (recur (inc i) (cond (nil? hash-data) vec-buffer :else (conj vec-buffer hash-data)) )
        )
      vec-buffer
      )
    )
  )

(defn db-get
  "Return the results of the select statement"
  [db-spec entity where-clauses limit-clause]
  (let [conn (jdbc/get-connection db-spec)
        sql (str "select " (get-record-keys-cl entity) " from " (get-record-name entity)
                 (cond (nil? where-clauses) "" :else (str " where " (where-cl where-clauses)))
                 (cond (nil? limit-clause) "" :else (limit-cl limit-clause)))
        prep-stmt (.prepareStatement conn sql)
        sql-ps (prep-statement prep-stmt where-clauses)
        results (.executeQuery sql-ps)]

    (let [vec-data (map-data results)
          _1 (.close prep-stmt)
          _2 (.close conn)]
      vec-data
      )
    )
  )

(defn db-insert
  "Add"
  ([db-spec entity]
   (let [
         conn (jdbc/get-connection db-spec)
         sql (str "SET IDENTITY_INSERT " (get-record-name entity) " OFF insert into "  (get-record-name entity) " (" (get-record-keys-cl entity) ") " "values (" (insert-cl entity) ") SET IDENTITY_INSERT " (get-record-name entity) " ON")
         prep-stmt (.prepareStatement conn sql Statement/RETURN_GENERATED_KEYS)
         sql-ps (prep-statement prep-stmt (get-record-values entity))
         results (.execute sql-ps)
         gk-rslt (.getGeneratedKeys sql-ps)
         _hasgk? (.next gk-rslt)
         _genKeys (cond (true? _hasgk?) (make-hash gk-rslt) :else nil)
         _1 (.close prep-stmt)
         _2 (.close conn)]
     _genKeys)
    )
  ([db-spec entity-definition entity]
   (let [
         conn (jdbc/get-connection db-spec)
         sql (str "SET IDENTITY_INSERT " (get-record-name entity) " OFF insert into "  (get-record-name entity) " (" (get-record-keys-cl entity-definition entity) ") " "values (" (insert-cl entity-definition entity)  ") SET IDENTITY_INSERT " (get-record-name entity) " ON")
         prep-stmt (.prepareStatement conn sql Statement/RETURN_GENERATED_KEYS)
         sql-ps (prep-statement prep-stmt (get-record-values entity-definition entity))
         results (.execute sql-ps)
         gk-rslt (.getGeneratedKeys sql-ps)
         _hasgk? (.next gk-rslt)
         _genKeys (cond (true? _hasgk?) (make-hash gk-rslt) :else nil)
         _1 (.close prep-stmt)
         _2 (.close conn)]
     _genKeys)
    )
  )

(defn db-update
  "update"
  ([db-spec entity where-clauses]
   (let [conn (jdbc/get-connection db-spec)
         sql (str "update "  (get-record-name entity) " set " (update-cl entity) " " (cond (nil? where-clauses) "" :else (str " where " (where-cl where-clauses))))
         prep-stmt (.prepareStatement conn sql)
         sql-ps (prep-statement prep-stmt (get-record-values entity) where-clauses)
         results (.executeUpdate sql-ps)
         _1 (.close prep-stmt)
         _2 (.close conn)]
     results)
    )
  ([db-spec entity-definition entity where-clauses]
   (let [conn (jdbc/get-connection db-spec)
         sql (str "update "  (get-record-name entity) " set " (update-cl entity-definition entity) " " (cond (nil? where-clauses) "" :else (str " where " (where-cl where-clauses))))
         prep-stmt (.prepareStatement conn sql)
         sql-ps (prep-statement prep-stmt (get-record-values entity-definition entity) where-clauses)
         results (.executeUpdate sql-ps)
         _1 (.close prep-stmt)
         _2 (.close conn)]
     results)
    )
  )

(defn db-delete
  "Delete"
  [db-spec entity where-clauses]
  (def conn (jdbc/get-connection db-spec))
  (def sql (str "delete " (get-record-name entity) (cond (nil? where-clauses) "" :else (str " where " (where-cl where-clauses)))))
  (def prep-stmt (.prepareStatement conn sql))
  (def sql-ps (prep-statement prep-stmt where-clauses))
  (let [results (.executeUpdate sql-ps)
        _1 (.close prep-stmt)
        _2 (.close conn)]
    results)
  )

(defn db-count
  "Return the results of the select statement"
  [db-spec entity field where-clauses]
  (let [conn (jdbc/get-connection db-spec)
        sql (str "select count(" (str field) ") from " (get-record-name entity)
                 (cond (nil? where-clauses) "" :else (str " where " (where-cl where-clauses))))
        prep-stmt (.prepareStatement conn sql)
        sql-ps (prep-statement prep-stmt where-clauses)
        results (.executeQuery sql-ps)]

    (let [vec-data (map-data results)
          _1 (.close prep-stmt)
          _2 (.close conn) ]
      vec-data
      )
    )
  )

(defn db-proc
  "Return the results of the select statement"
  [db-spec ret-val? proc parameters]
  (let [conn (jdbc/get-connection db-spec)
        sql (str "{" (cond (false? ret-val?) "?="  :else "") " call " proc " " (cond (nil? parameters) "() }" :else (str " ( " (proc-cl parameters) " ) }")))
        prep-stmt (.prepareCall conn sql)
        sql-ps (proc-prep-statement prep-stmt parameters)
        results (cond (true? ret-val?) (.executeQuery sql-ps) :else (.execute sql-ps))
        ]
    (cond
      (in-outType? parameters)
      (let [vec-data (map-in-outType sql-ps parameters)
            _1 (.close prep-stmt)
            _2 (.close conn) ]
        vec-data)
      (true? ret-val?)
      (let [vec-data (map-data results)
            _1 (.close prep-stmt)
            _2 (.close conn) ]
        vec-data
        )
      :else []
      )
    )
  )

(defn db-query
  "Return the results of the select statement"
  [db-spec query]
  (let [conn (jdbc/get-connection db-spec)
        prep-stmt (.prepareStatement conn query)
        results (.executeQuery prep-stmt)]
    (let [vec-data (map-data results)
          _1 (.close prep-stmt)
          _2 (.close conn)]
      vec-data
      )
    )
  )

(defn db-query-update
  "Return the results of the select statement"
  [db-spec query]
  (let [conn (jdbc/get-connection db-spec)
        prep-stmt (.createStatement conn )
        _ (.executeUpdate prep-stmt query)]
    )
  )
