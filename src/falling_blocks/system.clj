(ns falling-blocks.system
  "Defines the main running system. Implemented using Component library."
  (:require [com.stuartsierra.component :as c]
            [falling-blocks.board :as brd]
            [falling-blocks.game-view :as gv]
            [falling-blocks.game :as g]))

(defn create-system
  []
  (c/system-map
    
    ;; The area on the view where pieces fall
    :board (brd/create-board)
    
    ;; TODO create a real component for this
    ;; The area on the view showing the next piece that's coming
    :next-up {:next-up-width 6 :next-up-height 6}
    
    ;; The displayed area. Encapsulates access to bocko-fun view
    :game-view (c/using (gv/create-game-view)
                        [:board :next-up])
    
    ;; The running game. Contains the game loop
    :game (c/using (g/create-game)
                   [:game-view :board :next-up])))

