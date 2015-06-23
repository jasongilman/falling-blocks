(ns falling-blocks.game
  "This namespace defines a Game record which handles the execution of a game."
  (:require [clojure.core.async :as a]
            [com.stuartsierra.component :as c]
            [falling-blocks.game-view :as gv]
            [falling-blocks.board :as b]
            [falling-blocks.keyboard :as keyboard]))

(def drop-msecs 
  "How long it takes for a piece to fall one row in milliseconds."
  1000)

(def space-key "␣")

(def down-key "↓")

(def left-key "←")

(def right-key "→")

(def key->command
  "A map of keys to commands they key press issues."
  {space-key :rotate
   down-key :down
   left-key :left
   right-key :right})

(defn- create-game-loop
  "Creates a game loop using a core async go block. Returns the go blocks channel.
  
  The main loop reads waits for a key to be pressed within a given period of time. If the time passes
  then the falling piece will drop one row. If a key is pressed then the key press is interpreted
  as a command to move the falling piece."
  [{:keys [game-view board key-channel next-up]}]
  
  ;; Create a core.async go block that will execute asynchronously
  (a/go 
    
    ;; The main loop. It starts with a timeout channel that will close when the piece should drop.
    (loop [timeout-chan (a/timeout drop-msecs)]
      
      ;; Read from either the timeout channel or the key channel.
      (let [[v port] (a/alts! [timeout-chan
                               key-channel]
                              :priority true)]
        (cond
          ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          ;; Did we timeout?
          (= port timeout-chan)
          ;; We timed out. Time to drop the current block
          (do
            ; (println "Dropping")
            (if (b/handle-command board :down)
              ; (println "Successfully dropped")
              (do 
                ; (println "Reached the bottom.")
                ;; TODO merge into board
                ;; get next up and add as a falling piece on the board
                ;; If the falling piece collides with the board initially they lose.
                ;; Throw exception that game is over.
                ))
            (gv/update-view game-view)
            (recur (a/timeout drop-msecs)))
          
          ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          ;; Was a key pressed?
          (and (some? v) (= port key-channel))
          ;; The user pressed a key. Handle it.
          (do
            (if-let [command (key->command v)]
              (do 
                (println "Handling " command)
                (b/handle-command board command)
                (gv/update-view game-view))
              (println "Ignoring key press" v))
            
            (recur timeout-chan))
          
          ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          ;; Did the key channel close?
          (and (nil? v) (= port key-channel))
          ;; If a nil value is read off a channel it means the channel was closed.
          ;; the key channel was closed. We're done here
          (println "Appears key channel was closed. Ending game loop")
          
          ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
          ;; Anything else is not expected
          :else
          (throw (Exception. (format "Unexpected state in game loop. v: %s port: %s" 
                                     (pr-str v) (pr-str port)))))))
    
    (println "game loop ending")))

(defrecord Game
  [
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
    ;; Close the key and game loop channel.
    (when key-channel (a/close! key-channel))
    (when game-loop-chan (a/close! game-loop-chan))
    (assoc this :game-loop-chan nil)))

(defn create-game
  []
  (map->Game {}))