(ns cljhelpers.excel-writer
  (:require [cljhelpers.validator :refer :all])
  (:import (org.apache.poi.xssf.usermodel XSSFWorkbook XSSFCell)
           (java.util Date)
           (java.io FileWriter FileOutputStream ByteArrayOutputStream)
           (javas FileUtil)))

(defn set-cell-value
  "Set cell value"
  [cell value]
  (cond (nil? value) nil :else (.setCellValue cell value))
  )

(defn cell-type
  [value]
  (cond
    (nil? value) XSSFCell/CELL_TYPE_STRING
    (instance? Boolean value) XSSFCell/CELL_TYPE_BOOLEAN
    (instance? Byte value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? Short value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? Integer value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? Long value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? Float value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? Double value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? BigDecimal value) XSSFCell/CELL_TYPE_NUMERIC
    (instance? String value) XSSFCell/CELL_TYPE_STRING
    (instance? Date value) XSSFCell/CELL_TYPE_STRING
    :else XSSFCell/CELL_TYPE_STRING
    )
  )

(defn- set-headers
  "Build the excel headers"
  [sheet headers row-start]
  (let [row (.createRow sheet row-start)]
    (loop [j 0]
      (if (< j (count headers))
        (let [cell (.createCell row j)]
          (set-cell-value cell (nth headers j))
          (recur (inc j))
          )
        )
      )
    )
  )

(defn- set-data
  "Build the excel data rows/columns"
  [sheet data-list row-start]
  (loop [i 0 curr-row row-start]
    (if (< i (count data-list))
      (let [row (.createRow sheet curr-row)
            data (nth data-list i)]
        (loop [j 0]
          (if (< j (count data))
            (let [value (nth data j)
                  type (cell-type value)
                  cell (.createCell row j type)]
              (set-cell-value cell (str value))
              (recur (inc j))
              )
            )
          )
        (recur (inc i) (inc curr-row))
        )
      )
    )
  )

(defn- set-data-params
  "Build the excel experiment params data rows/columns"
  [sheet data-params]
  (let [row1 (.createRow sheet 0)
        row2 (.createRow sheet 1)
        row3 (.createRow sheet 2)
        row4 (.createRow sheet 3)
        row5 (.createRow sheet 4)
        row6 (.createRow sheet 5)]
    (set-cell-value (.createCell row1 0) "Block Type")
    (set-cell-value (.createCell row2 0) "Chemistry")
    (set-cell-value (.createCell row3 0) "Experiment File Name")
    (set-cell-value (.createCell row4 0) "Experiment Run End Time")
    (set-cell-value (.createCell row5 0) "Instrument Type")
    (set-cell-value (.createCell row6 0) "Passive Reference")
    (set-cell-value (.createCell row1 1) (nth data-params 0))
    (set-cell-value (.createCell row2 1) (nth data-params 1))
    (set-cell-value (.createCell row3 1) (nth data-params 2))
    (set-cell-value (.createCell row4 1) (nth data-params 3))
    (set-cell-value (.createCell row5 1) (nth data-params 4))
    (set-cell-value (.createCell row6 1) (nth data-params 5))
    )
  )

(defn write-to-excel-generic
  "Given set of data, build the excel file based on headers and data"
  [file-name headers data-list]
  (let [workbook (XSSFWorkbook.)
        sheet (.createSheet workbook "Results")
        fos (FileOutputStream. file-name)]
    (set-headers sheet headers 0)
    (set-data sheet data-list 1)
    (.write workbook fos)
    (.close workbook)
    (.close fos)
    )
  )

(defn get-excel-generic-byte
  "Given set of data, build the excel file based on headers and data"
  [headers data-list]
  (let [workbook (XSSFWorkbook.)
        sheet (.createSheet workbook "Results")
        fos (ByteArrayOutputStream.)]
    (set-headers sheet headers 0)
    (set-data sheet data-list 1)
    (.write workbook fos)
    (.close workbook)
    (.close fos)
    fos
    )
  )

(defn write-to-excel-experiment
  "Given a set of data, build the excel file based on headers and data"
  [file-name experiment-params headers data-list]
  (let [workbook  (XSSFWorkbook.)
        sheet (.createSheet workbook "Results")
        fos (FileOutputStream. file-name)]
    (set-data-params sheet experiment-params)
    (set-headers sheet headers 7)
    (set-data sheet data-list 8)
    (.write workbook fos)
    (.close workbook)
    (.close fos)
    )
  )

