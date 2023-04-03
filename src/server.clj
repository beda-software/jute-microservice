(ns server
  (:refer-clojure :exclude [compile])
  (:require
   [org.httpkit.server :as http-server]
   [compojure.route :as route]
   [compojure.core :refer [defroutes, POST]]
   [jute.core :as jute]
   [fhirpath.core]))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn parse-jute-template [request]
  (let [{:keys [template context]} (:body request)
        fhirpath-definition {:fhirpath (fn
                                         ([expr] (fhirpath.core/fp expr context))
                                         ([expr scope] (fhirpath.core/fp expr scope)))}
        parsed-data ((jute/compile template) fhirpath-definition)
        entries (as-> parsed-data r
                  (get-in r [:body :entry])
                  (remove nil? r))]
    {:status 200
     :body (assoc-in parsed-data [:body :entry] entries)}))



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