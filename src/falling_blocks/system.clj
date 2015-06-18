(ns falling-blocks.system
  (:require [bocko.core :as b]
            [com.stuartsierra.component :as c]
            [falling-blocks.board :as brd]
            [falling-blocks.game-view :as gv]
            [falling-blocks.game :as g]))

(def board-width 10)
(def board-height 16)

(def next-up-width 6)
(def next-up-height 6)

(def border-size 2)

(defn create-system
  []
  (c/system-map
    :board (brd/create-board {:board-width board-width 
                              :board-height board-height})
    ;; TODO create a real component for this
    :next-up {:next-up-width next-up-width
              :next-up-height next-up-height}
    :game-view (c/using (gv/create-game-view {:border-size border-size})
                        [:board :next-up])
    :game (c/using (g/create-game)
                   [:game-view :board :next-up])))

