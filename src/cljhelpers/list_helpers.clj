(ns cljhelpers.list_helpers)

(defn LoadCmb [cmbnode datas]
  (-> cmbnode (.setItems datas))
  )