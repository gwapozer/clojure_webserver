(ns cljhelpers.email-utility
  (:require  [clojure.string])
  (:import (javax.mail Session Message Message$RecipientType)
           (javax.mail.internet MimeMessage InternetAddress)
           (java.util Date)
           [org.apache.commons.mail SimpleEmail]
           (org.apache.commons.mail SimpleEmail DefaultAuthenticator)
           )
  )

(defn- prefix-env
  "Short cut to easily prefix an environment variable."
  [prefix env-var]
  (let [prefix (if (not (empty? prefix)) (format "%s_" prefix) "")]
    (System/getenv
      (format "%s%s" prefix env-var))))

(defn- parse-bool
  "Parse a bool from a string."
  [bool-string]
  (let [bool-string (.toLowerCase (str bool-string))]
    (cond (= bool-string "yes") true
          (= bool-string "true") true
          (= bool-string "1") true
          true false)))

(defn send-message
  [server recipients subject message]
  (try
    (server recipients subject message)
    (catch Exception e
      (do
        (let [cause (if-not (nil? (.getCause e))
                      (.toString (.getCause e))
                      nil)]
          (hash-map :ok false
                    :type (.getClass e)
                    :message (.getMessage e)
                    :cause cause))))))

(defn mail-server
  [mail-host mail-port mail-ssl mail-user mail-pass mail-from]
  (fn [recipients subject message]
    (let [email (SimpleEmail.)
          mail-port (cond (string? mail-port) mail-port
                          (number? mail-port) (str mail-port)
                          :else mail-port)
          mail-from-name (re-find #"^[^<]+" mail-from)
          mail-from-name (if-not (nil? mail-from-name)
                           (clojure.string/replace mail-from-name
                                                   #"\s*$" "")
                           nil)
          mail-from-addr (re-find #"<.+>" mail-from )
          mail-from-addr (if-not (nil? mail-from-addr)
                           (clojure.string/replace mail-from-addr
                                                   #"<(.+)>" "$1")
                           nil)]
      (do
        (.setHostName email mail-host)
        (.setSslSmtpPort email mail-port)
        (.setSmtpPort email (Integer. mail-port))
        (.setTLS email mail-ssl)
        (doseq [recipient recipients]
          (.addTo email recipient))
        (cond (and (nil? mail-from-addr)
                   (nil? mail-from-name)) (.setFrom email
                                                    (format "%s@%s"
                                                            mail-user
                                                            mail-host))
              (nil? mail-from-addr) (.setFrom email mail-from-name)
              (nil? mail-from-name) (.setFrom email mail-from-addr)
              :else (.setFrom email mail-from-addr mail-from-name))
        (.setSubject email subject)
        (.setMsg email message)
        (.setAuthentication email mail-user mail-pass)
        (.send email)))))

(defn mail-server-from-env
  "Set up a mail server with environment variables."
  [& args]
  (let [prefix (if (> (count args) 0) (first args) "")
        mail-host (prefix-env prefix "MAIL_HOST")
        mail-port (prefix-env prefix "MAIL_PORT")
        mail-ssl  (parse-bool (prefix-env prefix "MAIL_SSL"))
        mail-user (prefix-env prefix "MAIL_USER")
        mail-pass (prefix-env prefix "MAIL_PASS")
        mail-from (prefix-env prefix "MAIL_FROM")]
    (mail-server mail-host mail-port mail-ssl mail-user mail-pass mail-from)))

(defn send-to
  "Synchronously send an email to a single recipient."
  ([server recipient subject message]
   (if (not (string? recipient))
     (hash-map :ok false
               :message "Invalid recipient."
               :cause "Recipient should be a string with a single address.")
     (send-message server [recipient] subject message))))

(defn send-mail
  "Synchronously send an email to a list of recipients."
  ([server recipients subject message]
   (if (and
         (not (vector? recipients))
         (not (vector? recipients)))
     (hash-map :ok false
               :message "Invalid recipients."
               :cause "Recipients should be a vector or list of addresses.")
     (send-message server recipients subject message))))

(defn send-to-async
  "Asynchronously send an email to a single recipient."
  [server recipient subject message]
  (future (send-to server recipient subject message)))

(defn send-mail-async
  "Asynchronously send email to a list of recipients."
  [server recipient subject message]
  (future (send-mail server recipient subject message)))

(defn send-email
  [smtp-protocol from to subj msg]
  (let [
        prop (System/getProperties)
        ;SSL Properties
        _ (.put prop "mail.smtp.user" (:smtp-user smtp-protocol))
        _ (.put prop "mail.smtp.password" (:smtp-passw smtp-protocol))
        _ (.put prop "mail.transport.protocol" "smtp")
        _ (.put prop "mail.smtp.host" (:smtp-server smtp-protocol))
        _ (.put prop "mail.smtp.port" (:smtp-port smtp-protocol))
        _ (.put prop "mail.smtp.starttls.enable" "true")
        _ (.put prop "mail.smtp.auth" "true")
        _ (.put prop "mail.smtp.socketFactory.port" (:smtp-port smtp-protocol))
        _ (.put prop "mail.smtp.socketFactory.class" "javax.net.ssl.SSLSocketFactory")
        _ (.put prop "mail.smtp.socketFactory.fallback" "true")
        _ (.put prop "mail.smtp.timeout" 25000)
        ;End

        session (Session/getDefaultInstance prop nil)
        message (MimeMessage. session)
        transport (.getTransport session "smtps")
        ]
    (do
      (.setFrom message (InternetAddress. from))
      (.setRecipients message (Message$RecipientType/TO) (InternetAddress/parse to false))
      (.setSubject message subj)
      (.setText message msg)
      (.setSentDate message (Date.))
      (.connect transport (:smtp-server smtp-protocol) (:smtp-port smtp-protocol) (:smtp-user smtp-protocol) (:smtp-passw smtp-protocol))
      (.sendMessage transport message (.getAllRecipients message))
      (.close transport)
      )
    )
  )

(defn simple-email
  [smtp-params from to subj body]
  (let [my-email (SimpleEmail.)
        _ (.setHostName my-email (:smtp-server smtp-params))
        _ (.setSmtpPort my-email (:smtp-port smtp-params))
        _ (.setAuthenticator my-email (DefaultAuthenticator. (:smtp-user smtp-params) (:smtp-passw smtp-params)))
        _ (.setSSLOnConnect my-email true)
        _ (.setFrom my-email from)
        _ (.setSubject my-email subj)
        _ (.setMsg my-email body)
        _ (loop [i 0] (if (< i (count to)) (let [em (nth to i) _ (.addTo my-email em)] (recur (inc i)))))
        ]
    (.send my-email)
    )
  )
