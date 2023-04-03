(ns server
  (:refer-clojure :exclude [compile])
  (:require
   [org.httpkit.server :as http-server]
   [compojure.route :as route]
   [compojure.core :refer [defroutes, POST]]
   [jute.core :as jute]
   [fhirpath.core]
   [clojure.data.json :as json]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn parse-jute-template [req]
  (let [context (json/read-str (:body req) :key-fn keyword)
        template (:template context)
        scope (:scope context)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (jute/compile template
                         {:fhirpath (fn
                                      ([expr] (fhirpath.core/fp expr scope))
                                      ([expr scope] (fhirpath.core/fp expr scope)))})}))


(defroutes all-routes
  (POST "/parse-template" [] parse-jute-template)
  (route/not-found "<p>Page not found.</p>"))

(defn run-server
  []
  (let [port (Integer/parseInt (or (System/getenv "APP_PORT") "8090"))]
    (reset! server (http-server/run-server all-routes {:port port}))
    (println (str "Runnning webserver at 0.0.0.0:" port))))

(comment

  (run-server)
  (stop-server))
:rcf

(defn -main
  [& _args]
  (run-server))