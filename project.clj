(defproject crudik "0.1.0-SNAPSHOT"
  :description "Simple CRUD app."
  :url "http://github.com/deniskolosov/crudik"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.postgresql/postgresql "42.1.4"] 
		               [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]
		               [org.clojure/clojure "1.10.1"]
															  [org.clojure/tools.namespace "1.0.0"]
															  [ring/ring-core "1.6.3"]
															  [ring/ring-jetty-adapter "1.6.3"]
															  [ring/ring-devel "1.6.3"]
															  [com.layerware/hugsql "0.4.8"]
															  [metosin/muuntaja "0.6.7"]
															  [ring-cors "0.1.13"]
															  [integrant "0.8.0"]
															  [integrant/repl "0.3.1"]
															  [metosin/reitit "0.5.2"]
															  [drift "1.5.2"]]
  :main ^:skip-aot crudik.core
  :ring {:handler crudik.core/app}
  :resource-paths ["cljs-src/resources"]
  :plugins [[lein-ring "0.12.5"]
            [drift "1.5.2"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
