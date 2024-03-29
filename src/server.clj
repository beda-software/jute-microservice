(ns server
  (:refer-clojure :exclude [compile])
  (:require
   [org.httpkit.server :as http-server]
   [compojure.route :as route]
   [compojure.core :refer [defroutes, POST, GET]]
   [jute.core :as jute]
   [clojure.data.json :as json]
   [clojure.walk :as walk]
   [ring.middleware.cors :refer [wrap-cors]]
   [fhirpath.core])
  (:gen-class))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn remove-nil [data]
  (walk/prewalk
    (fn [x]
      (cond
        (map? x)
        (into {}
              (remove (comp nil? val) x))
        (vector? x)
        (vec (remove nil? x))
        :else x))
    data))

(defn parse-jute-template [request]
  (let [body (json/read-str (slurp (:body request)) :key-fn keyword)
        {:keys [template context]} body
        fhirpath-definition {:fhirpath (fn
                                         ([expr] (fhirpath.core/fp expr context))
                                         ([expr scope] (fhirpath.core/fp expr scope)))}
        parsed-data ((jute/compile template) (merge fhirpath-definition context))
        clean (remove-nil parsed-data)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str clean)}))

(defroutes all-routes
  (GET "/health-check" [] (fn [_r] {:status 200 :body "ok"}))
  (POST "/parse-template" [] parse-jute-template)
  (route/not-found "<p>Page not found.</p>"))

(def handler
  (wrap-cors
    all-routes
    :access-control-allow-origin #".*"
    :access-control-allow-methods [:get :put :post :delete]))

(defn run-server
  []
  (let [port (Integer/parseInt (or (System/getenv "APP_PORT") "8090"))]
    (reset! server (http-server/run-server handler {:port port})) ;
    (println (str "Runnning webserver at 0.0.0.0:" port))))

(defn -main
  [& _args]
  (run-server))

(comment
  (do
    (@server)
    (run-server))

  )
