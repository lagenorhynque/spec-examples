# spec-examples

[![CircleCI](https://circleci.com/gh/lagenorhynque/spec-examples.svg?style=svg)](https://circleci.com/gh/lagenorhynque/spec-examples)

[clojure.spec](https://clojure.org/guides/spec) examples.

## Prerequisites

- build tool [Leiningen](https://leiningen.org/)
    - Mac OS X: `brew install leiningen`

- alternative: [`clj` command](https://clojure.org/guides/deps_and_cli)
    - Mac OS X: `brew install clojure`

## Experiment with REPL

- with Leiningen

```sh
$ lein repl
```

- with `clj` command

```sh
$ clj -R:dev
```

This will start Clojure REPL with the following code already executed:

```clj
(require '[clojure.spec.alpha :as s]
         '[clojure.spec.gen.alpha :as gen]
         '[clojure.spec.test.alpha :as stest])
```

## Run example code

- with Leiningen

```sh
$ lein run -m spec-examples.core
```

- with `clj` command

```sh
$ clj -m spec-examples.core
```

## Play [Blackjack](https://en.wikipedia.org/wiki/Blackjack)

In Clojure REPL,

```clj
user=> (require '[spec-examples.blackjack :as bj])
nil
user=> (bj/init)
dealer: [[9 :spade] ???]
player: [[4 :diamond] [:ace :diamond]] => 15
nil
user=> (bj/hit-by-player)
dealer: [[9 :spade] ???]
player: [[3 :club] [4 :diamond] [:ace :diamond]] => 18
nil
user=> (bj/stand-by-player)
dealer: [[9 :spade] [8 :diamond]] => 17
player: [[3 :club] [4 :diamond] [:ace :diamond]] => 18
You win!! x1
nil
```
### available commands

- `spec-examples.blackjack/init`: initialise the game
- `spec-examples.blackjack/hit-by-player`: hit operation by player
- `spec-examples.blackjack/stand-by-player`: stand operation by player
