(ns server
  (:require
   [org.httpkit.server :as http-server]
   [compojure.route :as route]
   [compojure.core :refer [defroutes, POST]]
   [jute.core :as jute]
   [org.httpkit.client :as client]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn parse-jute-template [req]

  (let [resources (:body req)]
    (println "Template: " (get resources "template"))
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body {"returned-template" (get resources "template" "test")}}))

(defroutes all-routes
  (POST "/parse-template" [] parse-jute-template)
  (route/not-found "<p>Page not found.</p>"))

(defn run-server
  []
  (let [port (Integer/parseInt (or (System/getenv "APP_PORT") "8090"))]
    (reset! server (http-server/run-server all-routes {:port port}))
    (println (str "Runnning webserver at 0.0.0.0:" port))))

(run-server)
(stop-server)

(defn -main
  [& _args]
  (run-server))