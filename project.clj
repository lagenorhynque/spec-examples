(defproject spec-examples "0.1.0-SNAPSHOT"
  :description "clojure.spec examples"
  :url "https://github.com/lagenorhynque/spec-examples"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC2"]]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :java-source-paths ["src/java"]
  :profiles
  {:dev {:dependencies [[org.clojure/test.check "0.9.0"]
                        [pjstadig/humane-test-output "0.8.3"]]
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :source-paths ["env/dev/clj"]}})
