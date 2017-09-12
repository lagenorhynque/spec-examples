(ns spec-examples.blackjack
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def ranks (into #{:ace :jack :queen :king} (range 2 (inc 10))))
(def suits #{:club :diamond :heart :spade})

(s/def ::card (s/tuple ranks suits))
(s/def ::hand (s/coll-of ::card
                         :distinct true
                         :min-count 2))

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
        :args (s/with-gen (s/cat :coll1 (s/coll-of nat-int?)
                                 :coll2 (s/coll-of nat-int?))
                #(gen/vector (s/gen (s/coll-of (s/int-in 0 100)))
                             2))
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
        :ret (s/tuple (s/coll-of ::card) ::deck)
        :fn (fn [{{:keys [deck]} :args
                  ret :ret}]
              (= (count deck)
                 (apply + (map count ret)))))

(defn- draw-cards
  ([deck]
   (draw-cards deck 1))
  ([deck n]
   (split-at n deck)))

(s/def ::deck (s/coll-of ::card
                         :distinct true))

(def initial-deck (for [r ranks
                        s suits]
                    [r s]))

(s/def ::dealer ::hand)
(s/def ::player ::hand)
(s/def ::reveal? boolean?)

(defn- game-generator
  []
  (letfn [(split-at-random-gen [deck]
            (gen/return (draw-cards deck
                                    (rand-int (count deck)))))]
    (gen/bind
     (gen/fmap shuffle (gen/return initial-deck))
     (fn [deck]
       (gen/bind
        (split-at-random-gen deck)
        (fn [[dealer deck']]
          (gen/bind
           (split-at-random-gen deck')
           (fn [[player deck'']]
             (gen/bind
              (gen/boolean)
              (fn [reveal?]
                (gen/return #::{:deck deck''
                                :dealer dealer
                                :player player
                                :reveal? reveal?})))))))))))

(s/def ::game (s/and (s/keys :req [::deck ::dealer ::player ::reveal?]
                             :gen game-generator)
                     (fn [{::keys [deck dealer player]}]
                       (apply distinct? (concat deck dealer player)))))

(s/fdef init-game
        :args (s/cat)
        :ret (s/and ::game
                    (fn [{::keys [dealer player reveal?]}]
                      (and (= (count dealer) (count player) 2)
                           (false? reveal?)))))

(defn init-game
  []
  (let [deck (shuffle initial-deck)
        [dealer deck'] (draw-cards deck 2)
        [player deck''] (draw-cards deck' 2)]
    #::{:deck deck''
        :dealer dealer
        :player player
        :reveal? false}))

(s/fdef status
        :args (s/cat :hand ::hand)
        :ret string?)

(defn status
  [hand]
  (str (vec hand) " => " (sum-hand hand)))

(s/fdef show-status
        :args (s/cat :game ::game)
        :ret nil?)

(defn show-status
  [{::keys [dealer player reveal?]}]
  (println "dealer:" (if reveal?
                       (status dealer)
                       (str "["(first dealer) " ???]")))
  (println "player:" (status player)))

(s/fdef hit
        :args (s/cat :game ::game
                     :turn #{::player ::dealer})
        :ret ::game
        :fn (letfn [(sum [{::keys [deck dealer player]}]
                      (+ (count deck) (count dealer) (count player)))]
              (fn [{{game-before :game} :args
                    game-after :ret}]
                (= (sum game-before) (sum game-after)))))

(defn hit
  [{::keys [deck] :as game} turn]
  (let [[[card] deck'] (draw-cards deck)]
    (assoc game
           ::deck deck'
           turn (cond-> (get game turn)
                  card (conj card)))))

(s/fdef stand
        :args (s/cat :game ::game)
        :ret (s/and ::game
                    (fn [{::keys [reveal?]}]
                      (true? reveal?))))

(defn stand
  [game]
  (assoc game ::reveal? true))

(s/def ::result #{:bj-win :win :draw :lose})

(s/fdef result
        :args (s/cat :dealer ::hand
                     :player ::hand)
        :ret ::result)

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

(s/fdef show-result
        :args (s/cat :game ::game)
        :ret nil?)

(defn show-result
  [{::keys [dealer player]}]
  (println (case (result dealer player)
             :bj-win "You win!!! x1.5"
             :win "You win!! x1"
             :draw "Draw!"
             :lose "You lose...")))

(let [g (atom (init-game))
      show #(show-status @g)
      hit-by-dealer #(while (< (sum-hand (::dealer @g)) 17)
                       (reset! g (hit @g ::dealer))
                       (show))]
  (defn init []
    (reset! g (init-game))
    (show))
  (defn hit-by-player []
    (reset! g (hit @g ::player))
    (show))
  (defn stand-by-player []
    (reset! g (stand @g))
    (show)
    (hit-by-dealer)
    (show-result @g)))
