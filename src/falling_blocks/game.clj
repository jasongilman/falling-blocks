(ns falling-blocks.game
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as c]
            [falling-blocks.game-view :as gv]
            [falling-blocks.keyboard :as keyboard]))

(defn- create-game-loop
  "TODO"
  [{:keys [game-view board next-up]}]
  ;; TODO start the keyboard channel
  (a/go 
    
    ;; TODO
    
    ))


(defrecord Game
  [
   ;; Config
   
   ;; Dependencies
   game-view
   board
   next-up
   
   ;; Running state
   game-loop-chan
   ]
  
  
  c/Lifecycle
  
  (start
    [this]
    (assoc this :game-loop-chan (create-game-loop this)))
  
  (stop
    [this]
    (when game-loop-chan (a/close! game-loop-chan))
    (assoc this :game-loop-chan nil)))



(defn create-game
  []
  (map->Game {}))