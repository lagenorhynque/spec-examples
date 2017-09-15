(ns spec-examples.core
  (:require [spec-examples.geometry :as geo]))

(defn -main [& args]
  (println "Hello, Clojure!")
  (let [shapes [#::geo{:type ::geo/sphere
                       :radius 3.0}
                #::geo{:type ::geo/cube
                       :side 3.0}
                #::geo{:type ::geo/cuboid
                       :side-a 3.0
                       :side-b 4.0
                       :side-c 5.0}]]
    (run! geo/show shapes)))
