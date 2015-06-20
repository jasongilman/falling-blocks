(ns falling-blocks.game
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as c]
            [falling-blocks.game-view :as gv]
            [falling-blocks.board :as b]
            [falling-blocks.keyboard :as keyboard]))

(def drop-msecs 1000)

(def key->command
  {"␣" :rotate
   "↓" :down
   "←" :left
   "→" :right})

(defn- create-game-loop
  "TODO"
  [{:keys [game-view board key-channel next-up]}]
  (a/go 
    
    (loop []
      (let [timeout-chan (a/timeout drop-msecs)
            [v port] (a/alts! [timeout-chan
                               key-channel]
                              :priority true)]
        (cond
          (= port timeout-chan)
          ;; We timed out. Time to drop the current block
          (do
            (println "Dropping")
            (if (b/handle-command board :down)
              (println "Successfully dropped")
              (do 
                (println "Reached the bottom.")
                ;; TODO merge into board
                ;; get next up and add as a falling piece on the board
                ;; If the falling piece collides with the board initially they lose.
                ;; Throw exception that game is over.
                ))
            (gv/update-view game-view)
            (recur))
          
          (and (some? v) (= port key-channel))
          ;; The user pressed a key. Handle it.
          (do
            (if-let [command (key->command v)]
              (do 
                (println "Handling " command)
                (b/handle-command board command)
                (gv/update-view game-view))
              (println "Ignoring key press" v))
            
            (recur))
          
          (and (nil? v) (= port key-channel))
          ;; the key channel was closed. We're done here
          (println "Appears key channel was closed. Ending game loop")
          
          :else
          (throw (Exception. (format "Unexpected state in game loop. v: %s port: %s" 
                                     (pr-str v) (pr-str port)))))))
    
    (println "game loop ending")))


(comment 
  
  (get-in user/system [:board])
  (get-in user/system [:game-view :bocko-view :canvas])
  
  )

(defrecord Game
  [
   ;; Config
   
   ;; Dependencies
   game-view
   board
   next-up
   
   ;; Running state
   key-channel
   game-loop-chan
   
   ]
  
  
  c/Lifecycle
  
  (start
    [this]
    (let [this (assoc this :key-channel (keyboard/create-key-channel 
                                          (get-in this [:game-view :bocko-view :canvas])))]
      (assoc this :game-loop-chan (create-game-loop this))))
  
  (stop
    [this]
    (when key-channel (a/close! key-channel))
    (when game-loop-chan (a/close! game-loop-chan))
    ;; Wait for it to close
    (a/<!! game-loop-chan)
    (assoc this :game-loop-chan nil)))



(defn create-game
  []
  (map->Game {}))