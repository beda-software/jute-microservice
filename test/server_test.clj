(ns server-test
  (:refer-clojure :exclude [compile, load])
  (:require [clojure.test :as t]
            [config-test]
            [org.httpkit.client :as client]
            [yaml.core :as yaml]
            [clojure.data.json :as json]
            [jute.core :as jute]
            [fhirpath.core]))

(t/use-fixtures :once config-test/setup-tests)

(t/deftest parse-template-valid
  (t/testing "Test jute/compile parse template correctly")
  (let [template (yaml/from-file "test/data/mapper.yaml" true)
        context {:QuestionnaireResponse (yaml/from-file "test/data/questionnaire_response.yaml" true)}
        {:keys [status body]} @(client/post "http://0.0.0.0:8090/parse-template"
                                            {:headers {"Content-Type" "application/json"}
                                             :body (json/write-str {:template template :context context})})
        extracted (json/read-str (slurp body) :key-fn keyword)
        expected (yaml/from-file "test/data/result_bundle.yaml" true)]
    (t/is (= status 200))
    (t/is (= expected extracted))))

(t/run-tests)

(comment
  (def template (yaml/from-file "test/data/mapper.yaml" true))
  (def context {:QuestionnaireResponse (yaml/from-file "test/data/questionnaire_response.yaml" true)})
  (def fhirpath-definition {:fhirpath (fn
                                        ([expr] (fhirpath.core/fp expr context))
                                        ([expr scope] (fhirpath.core/fp expr scope)))})

  (let [parsed-data ((jute/compile template) fhirpath-definition)
        entries (as-> parsed-data result
                  (get-in result [:body :entry])
                  (remove nil? result))]
    (assoc-in parsed-data [:body :entry] entries))) :rcf
