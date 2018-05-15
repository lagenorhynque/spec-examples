(ns spec-examples.geometry.specs
  (:require [clojure.spec.alpha :as s]
            [spec-examples.geometry :as geo]))

(s/def ::geo/type keyword?)
(s/def ::length (s/and number? pos?))
(s/def ::geo/radius ::length)
(s/def ::geo/side ::length)
(s/def ::geo/side-a ::length)
(s/def ::geo/side-b ::length)
(s/def ::geo/side-c ::length)

(defmulti shape-type ::geo/type)
(defmethod shape-type ::geo/sphere [_]
  (s/keys :req [::geo/radius]))
(defmethod shape-type ::geo/cube [_]
  (s/keys :req [::geo/side]))
(defmethod shape-type ::geo/cuboid [_]
  (s/keys :req [::geo/side-a ::geo/side-b ::geo/side-c]))

(s/def ::shape (s/multi-spec shape-type ::geo/type))

(s/fdef geo/surface-area
  :args (s/cat :shape ::shape)
  :ret number?)

(s/fdef geo/volume
  :args (s/cat :shape ::shape)
  :ret number?)

(s/fdef geo/show
  :args (s/cat :shape ::shape)
  :ret nil?)
