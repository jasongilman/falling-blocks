(ns falling-blocks.board
  (:require [falling-blocks.draw :as d]
            [falling-blocks.pieces :as p]))

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
   matrix-atom
   
   falling-piece-atom
   ]
  )

(defn board-raster
  "Returns a board raster showing what should be displayed with the current falling piece"
  [{:keys [matrix-atom falling-piece-atom]}]
  (let [matrix (deref matrix-atom)
        falling-piece (deref falling-piece-atom)]
    (p/apply-to-raster matrix falling-piece)))

(defn create-board
  [options]
  (let [{:keys [board-width
                board-height
                background-color] :as options} (merge default-options options)
        row (vec (repeat board-height background-color))]
    (map->Board
      (assoc options
             :matrix-atom (atom (vec (repeat board-width row)))
             :falling-piece-atom (atom (p/new-falling-piece (/ board-width 2)))))))