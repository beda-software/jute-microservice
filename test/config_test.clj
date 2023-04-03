(ns config-test
  (:require [server :as origin]))

(defn setup-tests
  [tests]
  (try
    (origin/run-server)
    (tests)
    (finally
      (origin/stop-server))))
