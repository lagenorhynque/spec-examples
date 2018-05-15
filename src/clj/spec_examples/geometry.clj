(ns spec-examples.geometry)

(defmulti surface-area ::type)
(defmethod surface-area ::sphere [{::keys [radius]}]
  (* 4 Math/PI (Math/pow radius 2)))
(defmethod surface-area ::cube [{::keys [side]}]
  (* 6 (Math/pow side 2)))
(defmethod surface-area ::cuboid [{::keys [side-a side-b side-c]}]
  (*' 2 (+' (*' side-a side-b)
            (*' side-b side-c)
            (*' side-c side-a))))

(defmulti volume ::type)
(defmethod volume ::sphere [{::keys [radius]}]
  (* 4/3 Math/PI (Math/pow radius 3)))
(defmethod volume ::cube [{::keys [side]}]
  (Math/pow side 3))
(defmethod volume ::cuboid [{::keys [side-a side-b side-c]}]
  (*' side-a side-b side-c))

(defn show
  [shape]
  (println shape)
  (println "  surface area:" (surface-area shape))
  (println "  volume:" (volume shape)))
