(ns pointsensor_dashboard.enums.enumerator)

(defn GetEnumByKey [seq id val]
  (first (filter #(= (-> % id) val) seq))
  )

(defn GetEnumListByKey [seq id val]
  (filter #(= (-> % id) val) seq)
  )

(defn GetEnumList [seq id]
  (filter #(-> % id) seq)
  )

(defn GetEnumValueList [seq id]
  (map #(-> % id) seq)
  )
