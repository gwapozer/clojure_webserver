(ns cljhelpers.excel-reader(:require [obj-eval.core :as oe]
                                     [dk.ative.docjure.spreadsheet :refer :all]
                                     )
  (:import (org.apache.poi.ss.util CellReference)
           (org.apache.poi.ss.usermodel Row Cell)))

(defn- get-cell-data
  [curr-cell]
  (cond
    (nil? curr-cell) nil
    (= (.getCellType curr-cell) Cell/CELL_TYPE_STRING) (.getStringCellValue curr-cell)
    (= (.getCellType curr-cell) Cell/CELL_TYPE_NUMERIC) (.getNumericCellValue curr-cell)
    (= (.getCellType curr-cell) Cell/CELL_TYPE_BOOLEAN) (.getBooleanCellValue curr-cell)
    (= (.getCellType curr-cell) Cell/CELL_TYPE_FORMULA) (.getCellFormula curr-cell)
    (= (.getCellType curr-cell) Cell/CELL_TYPE_BLANK) ""
    :else nil
    )
  )

(defn- get-row-values
  ([row col-map col-keys]
   (def last-cell-num (.getLastCellNum row))
   (loop [j 0 hash-value {}]
     (if (< j last-cell-num)
       (do
         (def cell (-> row (.getCell j Row/RETURN_BLANK_AS_NULL)))
         (def key-val (-> (read-string (str ":" (.toUpperCase (CellReference/convertNumToColString j)))) col-map))
         (def cell-hash {key-val (get-cell-data cell)})
         (recur (inc j)
                (cond (some #(= (str %) (str ":" (.toUpperCase (CellReference/convertNumToColString j)))) (into [] col-keys))
                      (conj hash-value cell-hash) :else hash-value))
         )
       hash-value
       )
     )
    )

  ([row]
   (def last-cell-num (.getLastCellNum row))
   (loop [j 0 hash-value {}]
     (if (< j last-cell-num)
       (do
         (def cell (-> row (.getCell j Row/RETURN_BLANK_AS_NULL)))
         (def key-val (read-string (str ":" (.toUpperCase (CellReference/convertNumToColString j)) (inc (.getRowNum row)))))
         (def cell-hash {key-val (get-cell-data cell)})
         (recur (inc j) (conj hash-value cell-hash))
         )
       hash-value
       )
     )
    )
  )

(defn get-value-by-cellname
  [sheet cellname]
  (let [cell-letter (re-seq #"[A-Z]+" cellname)
        cell-number (re-seq #"[0-9]+" cellname)]
    (doto sheet (.getRow (int cell-number)) (.getCell (CellReference/convertNumToColString cell-letter)))
    )
  )

(defn ReadExcelSheet
  "This will read an excel sheet but will ignore blank rows"
  [wb sheet-name]
  (def sheet (select-sheet sheet-name wb))

  (def iter-exec (.iterator sheet))

  (def row-cnt 0)

  (while (.hasNext iter-exec)
    (def curr-row (.next iter-exec))
    (def cell-iter (.iterator curr-row))
    (def cell-cnt 0)

    (while (.hasNext cell-iter)
      (def curr-cell (.next cell-iter))
      (def cell-val (get-cell-data curr-cell))
      ;(oe/eval-obj 'dx-prototype.experiment.core [(str "RowCellNum[" row-cnt "][" cell-cnt "] ->" cell-val) ] "Sheet reader")
      (def cell-cnt (inc cell-cnt))
      )
    (def row-cnt (inc row-cnt))
    )
  )

(defn ReadExcel
  "This will read an excel sheet based on col-map, will not skip blank row needed to validate excel structure"
  ([sheet col-map]
   (def col-keys (keys col-map))
   (def col-vals (vals col-map))

   (def last-row-num (.getLastRowNum sheet))

   (loop [i 0 vec-value []]
     (if (<= i last-row-num)
       (do
         (def row (.getRow sheet i))
         (def row-data nil)
         (if-not (nil? row)
           (def row-data (get-row-values row col-map col-keys))
           )
         (recur (inc i) (conj vec-value row-data))
         )
       vec-value
       )
     ))

  ([sheet]
   (def last-row-num (.getLastRowNum sheet))

   (loop [i 0 vec-value []]
     (if (<= i last-row-num)
       (do
         (def row (.getRow sheet i))
         (def row-data nil)
         (if-not (nil? row)
           (def row-data (get-row-values row))
           )
         (recur (inc i) (conj vec-value row-data))
         )
       vec-value
       )
     ))
  )