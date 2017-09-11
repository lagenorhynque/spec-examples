(ns spec-examples.geometry
  (:require [clojure.spec.alpha :as s]))

(s/def ::type keyword?)
(s/def ::pos-double (s/and double? pos?))
(s/def ::radius ::pos-double)
(s/def ::side-a ::pos-double)
(s/def ::side-b ::pos-double)
(s/def ::side-c ::pos-double)

(defmulti shape-type ::type)
(defmethod shape-type ::sphere [_]
  (s/keys :req [::radius]))
(defmethod shape-type ::cube [_]
  (s/keys :req [::side-a]))
(defmethod shape-type ::cuboid [_]
  (s/keys :req [::side-a ::side-b ::side-c]))

(s/def ::shape (s/multi-spec shape-type ::type))

(s/fdef area
        :args (s/cat :shape ::shape)
        :ret double?)

(defmulti area ::type)
(defmethod area ::sphere [{::keys [radius]}]
  (* 4 Math/PI (Math/pow radius 2)))
(defmethod area ::cube [{::keys [side-a]}]
  (* 6 (Math/pow side-a 2)))
(defmethod area ::cuboid [{::keys [side-a side-b side-c]}]
  (* 2 (+ (* side-a side-b)
          (* side-b side-c)
          (* side-c side-a))))

(s/fdef volume
        :args (s/cat :shape ::shape)
        :ret double?)

(defmulti volume ::type)
(defmethod volume ::sphere [{::keys [radius]}]
  (* 4/3 Math/PI (Math/pow radius 3)))
(defmethod volume ::cube [{::keys [side-a]}]
  (Math/pow side-a 3))
(defmethod volume ::cuboid [{::keys [side-a side-b side-c]}]
  (* side-a side-b side-c))