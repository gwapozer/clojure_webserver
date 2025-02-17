(defproject pointsensor_dashboard "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repl-options {:init-ns pointsensor_dashboard.test}
  :dependencies [[mount "0.1.7"]
                 [cprop "0.1.9"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.4.490"]
                 [zip4j "1.3.2"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [org.apache.shiro/shiro-core "1.3.2"]
                 [org.apache.shiro/shiro-ehcache "1.3.2"]
                 [org.apache.poi/poi "3.14"]
                 [org.apache.poi/poi-ooxml "3.14"]
                 [h2 "1.4.199"]
                 [mssql-jdbc "7.0.0"]
                 [hiccup "1.0.4"]
                 [compojure "1.1.6"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-codec "1.1.2"]
                 [jline "0.9.94"]
                 [com.sun.mail/javax.mail "1.6.2"]
                 [org.apache.commons/commons-email "1.5"]
                 ]
  :main pointsensor_dashboard.core
  :target-path "target/%s"
  :resource-paths ["resources", "resources/FxFiles"]
  :java-source-paths ["src/javas"]
  :profiles {:uberjar {:aot [pointsensor_dashboard.core]
                       ;:omit-source true
                       ;:all
                       }}

  )
