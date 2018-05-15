(ns spec-examples.blackjack)

(def ranks (into #{:ace :jack :queen :king} (range 2 (inc 10))))
(def suits #{:club :diamond :heart :spade})

(defn- card->points
  [[rank]]
  (cond
    (= rank :ace) [1 11]
    (nat-int? rank) [rank]
    :else [10]))

(defn- plus-each
  [coll1 coll2]
  (for [elem1 coll1
        elem2 coll2]
    (+ elem1 elem2)))

(defn sum-hand
  [hand]
  (let [score-candidates (->> hand
                              (map card->points)
                              (reduce plus-each [0]))
        no-busts (filter #(<= % 21) score-candidates)]
    (if (empty? no-busts)
      (first score-candidates)
      (apply max no-busts))))

(defn- draw-cards
  ([deck]
   (draw-cards deck 1))
  ([deck n]
   (split-at n deck)))

(def initial-deck (for [r ranks
                        s suits]
                    [r s]))

(defn init-game
  []
  (let [deck (shuffle initial-deck)
        [dealer deck'] (draw-cards deck 2)
        [player deck''] (draw-cards deck' 2)]
    #::{:deck deck''
        :dealer dealer
        :player player
        :reveal? false}))

(defn status
  [hand]
  (str (vec hand) " => " (sum-hand hand)))

(defn show-status
  [{::keys [dealer player reveal?]}]
  (println "dealer:" (if reveal?
                       (status dealer)
                       (str "["(first dealer) " ???]")))
  (println "player:" (status player)))

(defn hit
  [{::keys [deck] :as game} turn]
  (let [[[card] deck'] (draw-cards deck)]
    (assoc game
           ::deck deck'
           turn (cond-> (get game turn)
                  card (conj card)))))

(defn stand
  [game]
  (assoc game ::reveal? true))

(defn result
  [dealer player]
  (let [dealer-pt (sum-hand dealer)
        player-pt (sum-hand player)]
    (cond
      (and (= player-pt 21)             ; player blackjack
           (= (count player) 2)
           (or (not= dealer-pt 21)
               (and (= dealer-pt 21)
                    (not= (count dealer) 2))))
      :bj-win
      (or (and (> dealer-pt 21)         ; dealer bust
               (<= player-pt 21))
          (and (< dealer-pt player-pt)  ; player higher
               (<= player-pt 21)))
      :win
      (or (and (= dealer-pt player-pt 21) ; blackjack tie
               (= (count dealer) (count player) 2))
          (and (= dealer-pt player-pt)  ; normal tie
               (or (< dealer-pt 21)
                   (and (= dealer-pt 21)
                        (not= (count dealer) 2)))))
      :draw
      :else
      :lose)))

(defn show-result
  [{::keys [dealer player]}]
  (println (case (result dealer player)
             :bj-win "You win!!! x1.5"
             :win "You win!! x1"
             :draw "Draw!"
             :lose "You lose...")))
