(ns spec-examples.geometry
  (:require [clojure.spec.alpha :as s]))

(s/def ::type keyword?)
(s/def ::radius (s/and double? pos?))
(s/def ::side-a (s/and double? pos?))
(s/def ::side-b (s/and double? pos?))
(s/def ::side-c (s/and double? pos?))

(defmulti shape-type ::type)
(defmethod shape-type ::sphere [_]
  (s/keys :req [::radius]))
(defmethod shape-type ::cube [_]
  (s/keys :req [::side-a]))
(defmethod shape-type ::cuboid [_]
  (s/keys :req [::side-a ::side-b ::side-c]))

(s/def ::shape (s/multi-spec shape-type ::type))

(s/fdef volume
        :args (s/cat :shape ::shape)
        :ret double?)

(defmulti volume ::type)
(defmethod volume ::sphere [{::keys [radius]}]
  (* 4/3 Math/PI radius radius radius))
(defmethod volume ::cube [{::keys [side-a]}]
  (* side-a side-a side-a))
(defmethod volume ::cuboid [{::keys [side-a side-b side-c]}]
  (* side-a side-b side-c))
