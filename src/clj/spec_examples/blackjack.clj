(ns spec-examples.blackjack
  (:require [clojure.spec.alpha :as s]))

(def ranks (into #{:ace :jack :queen :king} (range 2 (inc 10))))
(def suits #{:club :diamond :heart :spade})
(def initial-deck (for [r ranks
                        s suits]
                    [r s]))

(s/def ::card (s/tuple ranks suits))
(s/def ::hand (s/coll-of ::card
                         :distinct true
                         :min-count 1))

(s/def ::deck (s/coll-of ::card))
(s/def ::dealer ::hand)
(s/def ::player ::hand)
(s/def ::reveal? boolean?)
(s/def ::game (s/keys :req [::deck ::dealer ::player ::reveal?]))

(s/fdef card->points
        :args (s/cat :card ::card)
        :ret (s/coll-of (s/int-in 1 (inc 11))
                        :min-count 1))

(defn- card->points
  [[rank]]
  (cond
    (= rank :ace) [1 11]
    (nat-int? rank) [rank]
    :else [10]))

(s/fdef plus-each
        :args (s/cat :coll1 (s/coll-of nat-int?)
                     :coll2 (s/coll-of nat-int?))
        :ret (s/coll-of nat-int?))

(defn- plus-each
  [coll1 coll2]
  (for [elem1 coll1
        elem2 coll2]
    (+ elem1 elem2)))

(s/fdef sum-hand
        :args (s/cat :hand ::hand)
        :ret nat-int?)

(defn sum-hand
  [hand]
  (let [score-candidates (->> hand
                              (map card->points)
                              (reduce plus-each [0]))
        no-busts (filter #(<= % 21) score-candidates)]
    (if (empty? no-busts)
      (first score-candidates)
      (apply max no-busts))))

(s/fdef draw-cards
        :args (s/cat :deck ::deck
                     :n (s/? nat-int?))
        :ret (s/tuple (s/coll-of ::card) ::deck))

(defn- draw-cards
  ([deck]
   (draw-cards deck 1))
  ([deck n]
   (split-at n deck)))

(s/fdef init-game
        :args (s/cat)
        :ret ::game)

(defn init-game
  []
  (let [deck (shuffle initial-deck)
        [dealer deck'] (draw-cards deck 2)
        [player deck''] (draw-cards deck' 2)]
    #::{:deck deck''
        :dealer dealer
        :player player
        :reveal? false}))

(s/fdef show-status
        :args (s/cat :game ::game)
        :ret nil?)

(defn show-status
  [{::keys [dealer player reveal?] :as game}]
  (letfn [(status [hand]
            (str (vec hand) " => " (sum-hand hand)))]
    (println "dealer:" (if reveal?
                         (status dealer)
                         (str "["(first dealer) " ???]")))
    (println "player:" (status player))))

(s/fdef hit
        :args (s/cat :game ::game
                     :turn #{::player ::dealer})
        :ret ::game)

(defn hit
  [{::keys [deck] :as game} turn]
  (let [[[card] deck'] (draw-cards deck)]
    (assoc game
           ::deck deck'
           turn (conj (get game turn) card))))

(s/fdef stand
        :args (s/cat :game ::game)
        :ret ::game)

(defn stand
  [game]
  (assoc game ::reveal? true))

(let [g (atom (init-game))]
  (defn show []
    (show-status @g))
  (defn init []
    (reset! g (init-game))
    (show))
  (defn hit-by-player []
    (reset! g (hit @g ::player))
    (show))
  (defn stand-by-player []
    (reset! g (stand @g))
    (show))
  (defn hit-by-dealer []
    (reset! g (hit @g ::dealer))
    (show)))
