(ns spec-examples.geometry
  (:require [clojure.spec.alpha :as s]))

(s/def ::type keyword?)
(s/def ::radius number?)
(s/def ::side-a number?)
(s/def ::side-b number?)
(s/def ::side-c number?)

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
        :ret number?)

(defmulti volume ::type)
(defmethod volume ::sphere [{::keys [radius]}]
  (* 4/3 Math/PI radius radius radius))
(defmethod volume ::cube [{::keys [side-a]}]
  (* side-a side-a side-a))
(defmethod volume ::cuboid [{::keys [side-a side-b side-c]}]
  (* side-a side-b side-c))
