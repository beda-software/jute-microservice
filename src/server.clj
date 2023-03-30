(ns server
  (:require
   [org.httpkit.server :as server]
   [compojure.route :as route]
   [compojure.core :refer [defroutes, POST]]
   ;[com.health-samurai/jute :as jute]
   ))

(defn parse-jute-template [req]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body {:test "Test text"}})

(defroutes all-routes
  (POST "/parse-template" [] parse-jute-template)
  (route/not-found "<p>Page not found.</p>"))

(defn run
  "Run the web server"
  []
  (let [port (Integer/parseInt (or (System/getenv "APP_PORT") "8090"))]
    (server/run-server all-routes {:port port})
    (println (str "Runnning webserver at 0.0.0.0:" port))))

(defn -main
  [& _args]
  (run))