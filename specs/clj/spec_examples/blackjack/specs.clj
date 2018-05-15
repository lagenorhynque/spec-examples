(ns spec-examples.blackjack.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.test.check.generators :as gen]
            [spec-examples.blackjack :as bj]))

(s/def ::card (s/tuple bj/ranks bj/suits))
(s/def ::hand (s/coll-of ::card
                         :distinct true
                         :min-count 2))

(s/fdef bj/card->points
  :args (s/cat :card ::card)
  :ret (s/coll-of (s/int-in 1 (inc 11))
                  :min-count 1))

(s/fdef bj/plus-each
  :args (s/with-gen (s/cat :coll1 (s/coll-of nat-int?)
                           :coll2 (s/coll-of nat-int?))
          #(sgen/vector (s/gen (s/coll-of (s/int-in 0 100)))
                        2))
  :ret (s/coll-of nat-int?))

(s/fdef bj/sum-hand
  :args (s/cat :hand ::hand)
  :ret nat-int?)

(s/def ::bj/deck (s/coll-of ::card
                            :distinct true))

(s/fdef bj/draw-cards
  :args (s/cat :deck ::bj/deck
               :n (s/? nat-int?))
  :ret (s/tuple (s/coll-of ::card) ::bj/deck)
  :fn (fn [{{:keys [deck]} :args
            ret :ret}]
        (= (count deck)
           (apply + (map count ret)))))

(s/def ::bj/dealer ::hand)
(s/def ::bj/player ::hand)
(s/def ::bj/reveal? boolean?)

(defn- game-generator
  []
  (letfn [(split-at-random-gen [deck]
            (sgen/return (#'bj/draw-cards deck
                                          (rand-int (count deck)))))]
    (gen/let [deck (sgen/fmap shuffle (sgen/return bj/initial-deck))
              [dealer deck'] (split-at-random-gen deck)
              [player deck''] (split-at-random-gen deck')
              reveal? (sgen/boolean)]
      #::bj{:deck deck''
            :dealer dealer
            :player player
            :reveal? reveal?})))

;; verbose implementation with sgen/bind & sgen/return
(defn- game-generator'
  []
  (letfn [(split-at-random-gen [deck]
            (sgen/return (#'bj/draw-cards deck
                                          (rand-int (count deck)))))]
    (sgen/bind
     (sgen/fmap shuffle (sgen/return bj/initial-deck))
     (fn [deck]
       (sgen/bind
        (split-at-random-gen deck)
        (fn [[dealer deck']]
          (sgen/bind
           (split-at-random-gen deck')
           (fn [[player deck'']]
             (sgen/bind
              (sgen/boolean)
              (fn [reveal?]
                (sgen/return #::bj{:deck deck''
                                   :dealer dealer
                                   :player player
                                   :reveal? reveal?})))))))))))

(s/def ::game (s/and (s/keys :req [::bj/deck ::bj/dealer ::bj/player ::bj/reveal?]
                             :gen game-generator)
                     (fn [{::bj/keys [deck dealer player]}]
                       (apply distinct? (concat deck dealer player)))))

(s/fdef bj/init-game
  :args (s/cat)
  :ret (s/and ::game
              (fn [{::bj/keys [dealer player reveal?]}]
                (and (= (count dealer) (count player) 2)
                     (false? reveal?)))))

(s/fdef bj/status
  :args (s/cat :hand ::hand)
  :ret string?)

(s/fdef bj/show-status
  :args (s/cat :game ::game)
  :ret nil?)

(s/fdef bj/hit
  :args (s/cat :game ::game
               :turn #{::bj/player ::bj/dealer})
  :ret ::game
  :fn (letfn [(sum [{::bj/keys [deck dealer player]}]
                (+ (count deck) (count dealer) (count player)))]
        (fn [{{game-before :game} :args
              game-after :ret}]
          (= (sum game-before) (sum game-after)))))

(s/fdef bj/stand
  :args (s/cat :game ::game)
  :ret (s/and ::game
              (fn [{::bj/keys [reveal?]}]
                (true? reveal?))))

(s/def ::result #{:bj-win :win :draw :lose})

(s/fdef bj/result
  :args (s/cat :dealer ::hand
               :player ::hand)
  :ret ::result)

(s/fdef bj/show-result
  :args (s/cat :game ::game)
  :ret nil?)
