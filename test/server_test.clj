(ns server-test
  (:require [clojure.test :as t]
            [config-test]
            [org.httpkit.client :as client]
            [yaml.core :as yaml]
            [clojure.data.json :as json]))

(t/use-fixtures :once config-test/setup-tests)
(t/deftest parse-template-valid
  (t/testing "Context of the test assertions")
  (let [mapper (yaml/from-file "test/data/mapper.yaml" true)
        questionnaire-response (yaml/from-file "test/data/questionnaire-response.yaml" true)
        result @(client/post "http://0.0.0.0:8090/parse-template"
                             {:headers {"Content-Type" "application/json"}
                              :body (json/write-str {:mapper mapper
                                                     :scope {:QuestionnaireResponse questionnaire-response}})}
                             (fn [{:keys [status body]}]
                               (println "status is: " status)
                               body))
        expected (yaml/from-file "test/data/result_bundle.yaml" true)]
    (println "Result is: " (json/read-str result :key-fn keyword))
    (t/is (= expected result))))

(t/run-tests)