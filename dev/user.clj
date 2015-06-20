(ns user
  (:require [clojure.pprint :refer (pprint pp)]
            [clojure.test :refer (run-all-tests)]
            [clojure.tools.namespace.repl :as tnr]
            [clojure.repl :refer :all]
            [com.stuartsierra.component :as c]
            [falling-blocks.system :as s]
            [alex-and-georges.debug-repl :refer :all]
            [i]))

(def system nil)

(defn start []
  (set! *print-length* 50)
  (set! *print-level* 50)
  (let [the-system (s/create-system)]
    (alter-var-root #'system
                    (constantly (c/start the-system))))
  nil)

(defn stop []
  (alter-var-root #'system #(when % (c/stop %))))

(defn reset []
  (stop)
  (tnr/refresh :after 'user/start))

(println "falling blocks user.clj loaded.")