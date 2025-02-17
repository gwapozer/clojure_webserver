(ns pointsensor-dashboard.entity.sensor
  (:require [cljhelpers.datastruct :refer :all])
  )

(defrecord sensor [id
                   Ident
                   SerNo
                   Firmware
                   Kind
                   AddedAt
                   LiveAt
                   DeletedAt
                   ConfigRemoteAt
                   ConfigLocalAt
                   Confirm
                   Name
                   Dsc
                   UserId
                   LocationId
                   UtcOffset
                   CoreId
                   MaxXmit
                   XmitPeriod
                   UdpTries
                   Hysteresis
                   AlarmExit
                   SamplePeriod
                   DataLogger
                   LogPeriod
                   LogIxBlock
                   LogIxRec
                   BeaconAtUtc
                   BeaconAt
                   HistoryAtUtc
                   HistoryAt
                   BeaconSeconds
                   HistorySeconds
                   BeaconMode
                   HistoryMode
                   GapXFact
                   OfflineTmMin
                   Online
                   PowerSupplyPercent
                   BatteryInstalled
                   BatteryLow
                   LinePower
                   Field1Name
                   Field1Minimum
                   Field1Maximum
                   Field1Resolution
                   Field1Offset
                   Field1CalOffset
                   Field1Decimals
                   Field1Units
                   Field1IsTemperature
                   Field1HighVal
                   Field1HighDur
                   Field1LowVal
                   Field1LowDur
                   Field2Name
                   Field2Minimum
                   Field2Maximum
                   Field2Resolution
                   Field2Offset
                   Field2CalOffset
                   Field2Decimals
                   Field2Units
                   Field2IsTemperature
                   Field2HighVal
                   Field2HighDur
                   Field2LowVal
                   Field2LowDur
                   Field3Name
                   Field3Minimum
                   Field3Maximum
                   Field3Resolution
                   Field3Offset
                   Field3CalOffset
                   Field3Decimals
                   Field3Units
                   Field3IsTemperature
                   Field3HighVal
                   Field3HighDur
                   Field3LowVal
                   Field3LowDur
                   Field4Name
                   Field4Minimum
                   Field4Maximum
                   Field4Resolution
                   Field4Offset
                   Field4CalOffset
                   Field4Decimals
                   Field4Units
                   Field4IsTemperature
                   Field4HighVal
                   Field4HighDur
                   Field4LowVal
                   Field4LowDur
                   DoNetworkMetrics
                   OriginId
                   Locator
                   XmitCount
                   ConnectMs
                   AckMs
                   RSSI
                   APMAC
                   ])

(defn get-sensor-fields []
  (seq (.getDeclaredFields sensor))
  )

(defn sensor-init []
  ;(empty-record sensor (seq (.getDeclaredFields sensor)))
  )

;(defn sensor-constructor [m]
;  (->sensor (:id m) nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
;            nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil
;            nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil)
;  )