(ns server-test
  (:refer-clojure :exclude [compile, load])
  (:require [clojure.test :as t]
            [config-test]
            [org.httpkit.client :as client]
            [yaml.core :as yaml]
            [clojure.data.json :as json]
            [jute.core :as jute]
            [fhirpath.core]
            [fhirpath.core :as sut]))

(t/use-fixtures :once config-test/setup-tests)
(t/deftest parse-template-valid
  (t/testing "Test jute/compile parse template correctly")
  (let [mapper (yaml/from-file "test/data/mapper.yaml" true)
        questionnaire-response (yaml/from-file "test/data/questionnaire-response.yaml" true)
        result @(client/post "http://0.0.0.0:8090/parse-template"
                             {:headers {"Content-Type" "application/json"}
                              :body (json/write-str {:template mapper
                                                     :scope {:QuestionnaireResponse questionnaire-response}})}
                             (fn [{:keys [status body]}]
                               (println "status is: " status)
                               body))
        expected (yaml/from-file "test/data/result_bundle.yaml" true)]
    (println "Result is: " result)
    (t/is (= expected result))))

(t/run-tests)

(comment
  (def template (yaml/from-file "test/data/mapper.yaml" true))
  (def context {:QuestionnaireResponse (yaml/from-file "test/data/questionnaire_response.yaml" true)})
  (def fhirpath-definition {:fhirpath (fn
                                        ([expr] (fhirpath.core/fp expr context))
                                        ;([expr scope] (fhirpath.core/fp expr scope))
                                        )})
  ((jute/compile template) fhirpath-definition))
:rcf
