(ns falling-blocks.game-view
  "Contains a component that represents the game view and functions for manipulating it."
  (:require [bocko-fun.core :as b]
            [falling-blocks.raster :as r]
            [falling-blocks.board :as brd]
            [com.stuartsierra.component :as c]))

(def border-size
  "The width of the border around the view"
  2)

(defn update-view
  "Updates the view state to display the latest data on the board.
  
  This should be called manually when the view should update. Ideally we wouldn't do this. It should
  be hooked up via a watcher or similar."
  [{:keys [board next-up] :as game-view}]
  (let [updated-view (update-in game-view [:bocko-view :raster]
                                #(-> %
                                     (r/raster-replace (brd/board-raster board) border-size border-size)
                                     ;; TODO next up
                                     ))]
    (b/apply-raster! (:bocko-view updated-view))
    updated-view))

(defrecord GameView
  [
   ;; dependencies
   board
   next-up
   
   ;; Running state
   bocko-view
   ]
  
  c/Lifecycle
  
  (start
    [this]
    (let [{:keys [board-width board-height]} board
          {:keys [next-up-width]} next-up
          bocko-view (b/create-view {:width (+ border-size board-width next-up-width border-size)
                                     :height (+ border-size board-height border-size)
                                     :pixel-width 15
                                     :pixel-height 15
                                     :clear-color :black
                                     :default-color :white})]
      (-> this
          (assoc :bocko-view bocko-view)
          update-view)))
  
  (stop
    [this]
    (when bocko-view (b/close-view bocko-view))
    (assoc this :bocko-view nil)))

(defn create-game-view
  []
  (map->GameView {}))


