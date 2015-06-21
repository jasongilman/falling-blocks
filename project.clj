(defproject falling-blocks "0.1.0-SNAPSHOT"
  :description "Skeleton project for building tetris clone."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.stuartsierra/component "0.2.3"]
                 [bocko-fun "0.3.0"]]
  
  
  :profiles 
  {:dev {:source-paths ["dev" "src" "test"]
         :dependencies [[org.clojure/tools.namespace "0.2.10"]
                        [org.clojars.gjahad/debug-repl "0.3.3"]]}})


