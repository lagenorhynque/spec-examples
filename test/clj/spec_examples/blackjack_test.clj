(ns spec-examples.blackjack-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [spec-examples.blackjack :as bj]
            [spec-examples.test-util :refer [defspec-test]]))

(use-fixtures
  :once
  (fn [f]
    (stest/instrument)
    (f)
    (stest/unstrument)))

(deftest sum-hand-test
  (testing "J, Q, K are counted as 10"
    (is (= 12
           (bj/sum-hand [[2 :heart] [:jack :spade]])))
    (is (= 13
           (bj/sum-hand [[:queen :heart] [3 :spade]])))
    (is (= 14
           (bj/sum-hand [[:king :heart] [4 :spade]])))
    (is (= 20
           (bj/sum-hand [[:jack :heart] [:queen :spade]])))
    (is (= 20
           (bj/sum-hand [[:queen :heart] [:king :spade]])))
    (is (= 20
           (bj/sum-hand [[:king :heart] [:jack :spade]]))))
  (testing "A is counted as 1 or 11"
    (is (= 13
           (bj/sum-hand [[2 :heart] [:ace :spade]])))
    (is (= 21
           (bj/sum-hand [[:jack :heart] [:ace :spade]])))
    (is (= 21
           (bj/sum-hand [[:jack :heart] [10 :spade] [:ace :diamond]])))
    (is (= 21
           (bj/sum-hand [[:king :heart] [5 :spade] [:ace :diamond] [5 :club]])))
    (is (= 15
           (bj/sum-hand [[:ace :heart] [:king :spade] [3 :diamond] [:ace :club]]))))
  (testing "when the score exceeds 21 (busts), returns score larger than 21"
    (is (< 21
           (bj/sum-hand [[10 :heart] [:queen :spade] [2 :diamond]])))
    (is (< 21
           (bj/sum-hand [[:ace :heart] [2 :spade] [:king :diamond] [:queen :club]])))))

(deftest result-test
  (testing "win with blackjack"
    (is (= :bj-win
           (bj/result [[2 :diamond] [8 :club] [10 :spade]]
                      [[:ace :spade] [:queen :heart]])))
    (is (= :bj-win
           (bj/result [[4 :diamond] [8 :club] [10 :spade]]
                      [[:ace :spade] [:queen :heart]])))
    (is (= :bj-win
           (bj/result [[3 :diamond] [8 :club] [10 :spade]]
                      [[:ace :spade] [:queen :heart]]))))
  (testing "win without blackjack"
    (is (= :win
           (bj/result [[4 :diamond] [8 :club] [10 :spade]]
                      [[:ace :spade] [3 :heart] [7 :club]])))
    (is (= :win
           (bj/result [[4 :diamond] [8 :club] [10 :spade]]
                      [[:ace :spade] [3 :heart] [6 :club]])))
    (is (= :win
           (bj/result [[2 :diamond] [8 :club] [10 :spade]]
                      [[:ace :spade] [3 :heart] [7 :club]])))
    (is (= :win
           (bj/result [[2 :diamond] [7 :club] [10 :spade]]
                      [[:ace :spade] [3 :heart] [6 :club]]))))
  (testing "draw"
    (is (= :draw
           (bj/result [[:jack :diamond] [:ace :club]]
                      [[:ace :spade] [:queen :heart]])))
    (is (= :draw
           (bj/result [[2 :diamond] [8 :club] [10 :spade]]
                      [[3 :spade] [6 :heart] [:ace :club]])))
    (is (= :draw
           (bj/result [[:ace :diamond] [2 :club] [8 :spade]]
                      [[:ace :spade] [3 :heart] [7 :club]]))))
  (testing "lose"
    (is (= :lose
           (bj/result [[:ace :diamond] [2 :club] [7 :spade]]
                      [[:ace :spade] [3 :heart] [8 :club]])))
    (is (= :lose
           (bj/result [[:ace :diamond] [2 :club] [7 :spade]]
                      [[:ace :spade] [3 :heart] [5 :club]])))
    (is (= :lose
           (bj/result [[:ace :diamond] [:king :club]]
                      [[:ace :spade] [3 :heart] [7 :club]])))))

(defspec-test sum-hand-spec-test [bj/sum-hand])
(defspec-test init-game-spec-test [bj/init-game])
(defspec-test status-spec-test [bj/status])
(defspec-test show-status-spec-test [bj/show-status])
(defspec-test hit-spec-test [bj/hit])
(defspec-test stand-spec-test [bj/stand])
(defspec-test result-spec-test [bj/result])
(defspec-test show-result-spec-test [bj/show-result])
