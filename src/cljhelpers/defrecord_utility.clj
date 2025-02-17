(ns cljhelpers.defrecord_utility)

(defn static? [field]
  (java.lang.reflect.Modifier/isStatic
    (.getModifiers field)))

(defn get-record-field-names [record-fields]
  (->> record-fields
       (remove static?)
       (map #(.getName %))
       (remove #{"__meta" "__extmap"})
       )
  )

(defmacro empty-record [record]
  (let [klass (Class/forName (name record))
        field-count (count (get-record-field-names klass))]
    `(new ~klass ~@(repeat field-count nil)))
  )

(defn get-record-parity
  [record-fields]
  (let [p (get-record-field-names record-fields)]
    p
    )
  )

(defn build-record-init
  [r k]
  (let [_vec (into [] (repeat (count k) nil))]
    (apply r _vec)
    )
  )

(defn build-record-data
  [r h k]
  (let [_keys (mapv #(read-string (str ":" (clojure.string/lower-case %))) k)
        _vec (mapv #(-> h %) _keys)]
    (apply r _vec)
    )
  )
