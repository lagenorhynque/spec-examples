(ns spec-examples.blackjack
  (:require [clojure.spec.alpha :as s]))

(def ranks (into #{:ace :jack :queen :king} (range 2 (inc 10))))
(def suits #{:club :diamond :heart :spade})

(s/def ::card (s/tuple ranks suits))
(s/def ::hand (s/coll-of ::card
                         :distinct true
                         :min-count 1))

(s/fdef card->points
        :args (s/cat :card ::card)
        :ret (s/+ (s/int-in 1 (inc 11))))

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
