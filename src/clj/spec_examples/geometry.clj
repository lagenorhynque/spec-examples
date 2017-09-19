(ns spec-examples.geometry
  (:require [clojure.spec.alpha :as s]))

(s/def ::type keyword?)
(s/def ::length (s/and number? pos?))
(s/def ::radius ::length)
(s/def ::side ::length)
(s/def ::side-a ::length)
(s/def ::side-b ::length)
(s/def ::side-c ::length)

(defmulti shape-type ::type)
(defmethod shape-type ::sphere [_]
  (s/keys :req [::radius]))
(defmethod shape-type ::cube [_]
  (s/keys :req [::side]))
(defmethod shape-type ::cuboid [_]
  (s/keys :req [::side-a ::side-b ::side-c]))

(s/def ::shape (s/multi-spec shape-type ::type))

(s/fdef surface-area
        :args (s/cat :shape ::shape)
        :ret number?)

(defmulti surface-area ::type)
(defmethod surface-area ::sphere [{::keys [radius]}]
  (* 4 Math/PI (Math/pow radius 2)))
(defmethod surface-area ::cube [{::keys [side]}]
  (* 6 (Math/pow side 2)))
(defmethod surface-area ::cuboid [{::keys [side-a side-b side-c]}]
  (*' 2 (+' (*' side-a side-b)
            (*' side-b side-c)
            (*' side-c side-a))))

(s/fdef volume
        :args (s/cat :shape ::shape)
        :ret number?)

(defmulti volume ::type)
(defmethod volume ::sphere [{::keys [radius]}]
  (* 4/3 Math/PI (Math/pow radius 3)))
(defmethod volume ::cube [{::keys [side]}]
  (Math/pow side 3))
(defmethod volume ::cuboid [{::keys [side-a side-b side-c]}]
  (*' side-a side-b side-c))

(s/fdef show
        :args (s/cat :shape ::shape)
        :ret nil?)

(defn show
  [shape]
  (println shape)
  (println "  surface area:" (surface-area shape))
  (println "  volume:" (volume shape)))
