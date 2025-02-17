(ns pointsensor_dashboard.widget.validation_display
  (:import (javax.swing JOptionPane JButton JLabel JTextArea JScrollPane JPanel JFrame)
           (java.awt BorderLayout))
  )

(defn Display-Validation-Msg [^String Title ^String msg]

  (def lbloutput
    (doto
      (JLabel. "Validation:")
      ))

  (def txtareafield (doto
                      (JTextArea. 20 57)
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
               (JFrame. Title)
               (.setSize 700 400)
               (.setContentPane panel)
               (.setResizable false)
               (.setVisible true)
               (.setLocationRelativeTo nil)
               ))
  )