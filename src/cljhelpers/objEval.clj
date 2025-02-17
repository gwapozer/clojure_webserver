(ns cljhelpers.objEval
  (:import (javax.swing JFrame JPanel JScrollPane JTextArea JButton JLabel JOptionPane)
           (java.awt BorderLayout)
           (java.awt.event ActionListener)))

(defn say-hello []
  (JOptionPane/showMessageDialog
    nil "Hello, World!" "Greeting"
    JOptionPane/INFORMATION_MESSAGE))

(def act (proxy [ActionListener] []
           (actionPerformed [event] (say-hello))))

(defn EvalInterface [^String SeqExplain ^String msg]

  (def button (doto
                (JButton. "Evaluate")
                (.addActionListener act)
                ))

  (def lbloutput
    (doto
      (JLabel. "Output data")
      ))

  (def txtareafield (doto
                      (JTextArea. 40 57)
                      (.setText msg)
                      (.setLineWrap true)
                      (.setWrapStyleWord true)))

  (def scrolltxtareafield
    (doto
      (JScrollPane. txtareafield (JScrollPane/VERTICAL_SCROLLBAR_ALWAYS) (JScrollPane/HORIZONTAL_SCROLLBAR_ALWAYS))
      )
    )

  (def txtareacmd (doto
                    (JTextArea. 4 40)
                    (.setLineWrap true)
                    (.setWrapStyleWord true)))

  (def scrolltxtcmd
    (doto
      (JScrollPane. txtareacmd (JScrollPane/VERTICAL_SCROLLBAR_ALWAYS) (JScrollPane/HORIZONTAL_SCROLLBAR_ALWAYS))
      )
    )


  (def txtareaoutput (doto
                       (JTextArea. 8 40)
                       (.setLineWrap true)
                       (.setWrapStyleWord true)))

  (def scrolltxtoutput
    (doto
      (JScrollPane. txtareaoutput (JScrollPane/VERTICAL_SCROLLBAR_ALWAYS) (JScrollPane/HORIZONTAL_SCROLLBAR_ALWAYS))
      )
    )

  (def panel (doto (JPanel.)
               ;(.add lbloutput)
               (.add scrolltxtareafield (BorderLayout/PAGE_START))
               ;(.add scrolltxtcmd (BorderLayout/PAGE_START))
               ;(.add scrolltxtoutput (BorderLayout/PAGE_START))
               ;(.add button (BorderLayout/PAGE_END))
               ))

  (def frame (doto
               (JFrame. SeqExplain)
               (.setSize 700 700)
               (.setContentPane panel)
               (.setResizable false)
               (.setVisible true)
               ))

  )

(def atom-evalcnt (atom 0))
(def atom-msg (atom ""))

(defn- reset-evaluator []
  (reset! atom-evalcnt 0)
  (reset! atom-msg ""))

(defn- inc-evalcnt []
  (swap! atom-evalcnt inc)
  )

(def const-msg ";EVALUATED ITEM->")

(defn- iterate-obj [s]
  (if (> (count (rest s)) 0)
    (do
      (println "Item:" (first s) )
      (iterate-obj (rest s))
      )
    (println (first s))
    )
  )

(defn- iterate-obj-cmd [cmd-list msg]
  (if (> (count (rest cmd-list)) 0)
    (do
      ;(println "Item:" (first cmd-list) )
      (def output (prn-str (first cmd-list)))
      (def newmsg (str  msg const-msg output))
      (iterate-obj-cmd (rest cmd-list) newmsg)
      )
    (let [output (prn-str (first cmd-list))]
      ;(println (first s))
      msg (str msg const-msg output))
    )
  )

(defn eval-obj [namespace cmd-list seqexplain]
  (require namespace)
  (inc-evalcnt)
  ;(def output (read-string (prn-str cmd)))
  (def output (iterate-obj-cmd cmd-list ""))
  (reset! atom-msg (str @atom-msg "Sequence: [" @atom-evalcnt  "] Explain: [" seqexplain "] " (str output)))
  ;(swg/EvalInterface (str "Sequence: [" @atom-evalcnt  "]" seqexplain) (str output))
  )

(defn load-gui-tracer[]
  (EvalInterface "Evaluating data" @atom-msg)
  (reset-evaluator)
  )