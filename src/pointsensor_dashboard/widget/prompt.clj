(ns pointsensor_dashboard.widget.prompt
  (:import (javax.swing JOptionPane JFileChooser JFrame JPanel)))

(defn prompt-message
  [title msg]
  (let [dialogResult (JOptionPane/showConfirmDialog nil (str msg) title (JOptionPane/YES_NO_OPTION))]
    dialogResult
    )
  )

(defn alert-message
  [title msg]
  (let [dialogResult (JOptionPane/showMessageDialog nil (str msg) title (JOptionPane/OK_OPTION))]
    dialogResult
    )
  )

(defn directory-file-chooser
  [title df-file df-path filters]
  (let [
        ;par-frame (JFrame.)
        frame (doto
                (JFrame. title)
                (.setSize 0 0)
                (.setResizable false)
                (.setVisible true)
                )

        fileChooser (JFileChooser. df-path)
        _ (.setDialogTitle fileChooser title)
        _ (.setFileFilter fileChooser filters)
        _ (.setSelectedFile fileChooser df-file)
        usr-selection (.showSaveDialog fileChooser frame)
        ]
    (if (= usr-selection (JFileChooser/APPROVE_OPTION))
      (do
        (.setVisible frame false)
        (.dispose frame)
        (.getSelectedFile fileChooser)
        )
      (do (.setVisible frame false) (.dispose frame) nil)
      )
    )
  )