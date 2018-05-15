(ns spec-examples.blackjack.play
  (:require [spec-examples.blackjack :as bj]))

(let [g (atom (bj/init-game))
      show #(bj/show-status @g)
      hit-by-dealer #(while (< (bj/sum-hand (::bj/dealer @g)) 17)
                       (reset! g (bj/hit @g ::bj/dealer))
                       (show))]
  (defn init []
    (reset! g (bj/init-game))
    (show))
  (defn hit []
    (reset! g (bj/hit @g ::bj/player))
    (show))
  (defn stand []
    (reset! g (bj/stand @g))
    (show)
    (hit-by-dealer)
    (bj/show-result @g)))
