(ns falling-blocks.board
  (:require [falling-blocks.draw :as d]))

(def default-options
  {:board-width 10
   :board-height 16
   :background-color :white})

(defrecord Board
  [
   board-width
   board-height
   background-color
   
   ;; A vector of vectors
   ;; inner vectors are columns
   matrix
   ]
  
  )

(defn create-board
  [options]
  (let [{:keys [board-width
                board-height
                background-color] :as options} (merge default-options options)
        row (vec (repeat board-height background-color))]
    (map->Board
      (assoc options
             :matrix (vec (repeat board-width row))))))