(ns spec-examples.geometry-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [spec-examples.geometry :as geo]
            [spec-examples.test-util :refer [defspec-test]]))

(use-fixtures
  :once
  (fn [f]
    (stest/instrument)
    (f)
    (stest/unstrument)))

(defspec-test surface-area-spec-test [geo/surface-area])
(defspec-test volume-spec-test [geo/volume])
(defspec-test show-spec-test [geo/show])
