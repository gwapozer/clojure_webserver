(ns cljhelpers.datastruct
  (:require  [cljhelpers.objEval :as oe]))

(defn find-in [m k]
  (if (map? m)
    (let [v (m k)]
      (->> m
           vals
           (map #(find-in % k)) ; Search in "child" maps
           (cons v) ; Add result from current level
           (filter (complement nil?))
           first))))

(defn find-key [m k]
  (loop [m' m]
    (when (seq m')
      (if-let [v (get m' k)]
        v
        (recur (reduce merge
                       (map (fn [[_ v]]
                              (when (map? v) v))
                            m')))))))

(defn find-key-in-edn
  [kw hash _val]
  (let [_find-val (find-in hash kw)]
    (if (-> _find-val nil? not)
      _find-val
      (if (-> hash empty? not)
        (let [curr (first hash)
              _map-entry? (map-entry? curr)
              value (cond _map-entry? (val curr) :else curr)
              _vec? (vector? value)
              _list? (list? value)
              key? (keyword? value)
              val-map-entry? (map? value)
              __val (cond (or _vec? _list?) (trampoline find-key-in-edn kw curr _val) :else (find-in curr kw))]
          (recur kw (rest hash) (cond (nil? __val) _val :else __val)))
        _val
        )
      )
    )
  )

(defn serializeToMap
  [serializeData]
  "Take a serialize jquery data, convert to map"
  )

(defn get-true-kw
  [kw]
  (let [_sp (clojure.string/split kw #"_")]
    (cond (= 1 (count _sp)) (apply str _sp) :else (apply str (butlast _sp)))
    )
  )

(defn- rename-vec-hash
  [my-list kw new-kw]
  (let [data-eval (mapv #(if (some (fn [x](= kw x)) (keys %)) (clojure.set/rename-keys % {kw new-kw}) %) my-list)]
    data-eval
    )
  )

(defn rename-vec-list
  [kw-list my-list]
  (loop [i 0 dt my-list]
    (if (< i (count kw-list))
      (let [kw (nth kw-list i)
            t-kw (read-string (get-true-kw (str kw)))
            _val (clojure.set/rename-keys dt {kw t-kw})]
        (recur (inc i) _val)
        )
      dt
      )
    )
  )

(defn get-num-list
  "Return the list of numbers of keys name ending in _"
  [hash-val]
  (let [ _keys (keys hash-val)
        n-list (map #(last (clojure.string/split (str %) #"_")) _keys)]
    (sort (distinct n-list))
    )
  )

(defn sort-keys
  "Sort all keys that have distinct ordering number"
  [_data -dt]
  (let [_nl (get-num-list _data)]
    (loop [i 0 vec-list -dt]
      (if (< i (count _nl))
        (let [_n (nth _nl i)
              _filtered-n (filterv #(= (last (clojure.string/split (str (key %) ) #"_")) _n) _data)
              _r-data (apply conj {} _filtered-n)]
          (recur (inc i) (conj vec-list _r-data))
          )
        vec-list
        )
      )
    )
  )

(defn group-keys
  "Group all keys that have distinct ordering number"
  [_data -dt]
  (let [_nl (get-num-list _data)]
    (loop [i 0 vec-list -dt]
      (if (< i (count _nl))
        (let [_n (nth _nl i)
              _filtered-n (filterv #(= (last (clojure.string/split (str (key %)) #"_")) _n) _data)
              _r-data (apply conj {} _filtered-n)
              kw-list (keys _r-data)
              __r-data (rename-vec-list kw-list _r-data)]
          (recur (inc i) (conj vec-list __r-data))
          )
        vec-list
        )
      )
    )
  )