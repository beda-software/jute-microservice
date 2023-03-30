(ns server-test
  (:require [clojure.test :as t]
            [config-test]
            [org.httpkit.client :as client]))

(t/use-fixtures :once config-test/setup-tests)

(t/deftest parse-template-valid
  (t/testing "Context of the test assertions")
  (let [response @(client/post "http://0.0.0.0:8090/parse-template"
                               {:body {"template" {"resource" "TestMapping"}}
                                :deadlock-guard? false}
                               (fn [{:keys [status body]}]
                                 (println "status is: " status)
                                 body))]
    (println "Response is: " response)))
(t/run-tests)