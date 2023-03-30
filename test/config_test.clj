(ns config-test
  (:require [server :as origin]))

(defn setup-tests
  [tests]
  (try
    (origin/-main)
    (tests)
    (finally
      (origin/stop-server))))
