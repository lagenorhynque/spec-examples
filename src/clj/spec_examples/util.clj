(ns spec-examples.util
  (:refer-clojure :exclude [let])
  (:require [clojure.core :as core]
            [clojure.core.specs.alpha :as specs]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/fdef let
        :args (s/cat :bindings ::specs/bindings
                     :body (s/* any?))
        :ret any?)

;; cf. clojure.test.check.generators/let
(defmacro let
  [bindings & body]
  (if (empty? bindings)
    `(gen/return (do ~@body))
    (core/let [[binding gen & more] bindings]
      `(gen/bind ~gen
                 (fn [~binding]
                   (let [~@more]
                     ~@body))))))
