(ns falling-blocks.pieces
  (:require [falling-blocks.raster :as r]))


(def long-piece
  {:positions (mapv r/transpose
                    [
                     ;; TODO describe what these are
                     [[:red :red :red :red]]
                     
                     [[:red]
                      [:red]
                      [:red]
                      [:red]]
                     
                     ])})

(defn new-falling-piece
  [horizontal-pos width height]
  {:location [horizontal-pos 0]
   :piece long-piece
   :position-index 1
   :board-height height
   :board-width width})

(defn to-raster
  [{:keys [piece position-index]}]
  (get-in piece [:positions position-index]))

(defn apply-to-raster
  [raster piece]
  (let [piece-raster (to-raster piece)
        [x y] (:location piece)]
    (r/raster-replace raster piece-raster x y)))

(defn on-board?
  "Returns true if the piece currently fits on the board"
  [{:keys [board-width board-height] 
    [x y] :location 
    :as piece}]
  (let [raster (to-raster piece)
        pos-height (r/raster-height raster)
        pos-width (r/raster-width raster)]
    (and (>= x 0)
         (<= (+ x pos-width) board-width)
         (>= y 0)
         (<= (+ y pos-height) board-height))))

(defn- coordinate-change
  "Updates the pieces location coordinate only if it can still stay on the board."
  [piece coordinate f]
  (let [location-index (get {:x 0 :y 1} coordinate)
        updated (update-in piece [:location location-index] f)]
    (if (on-board? updated)
      updated
      ;; The change would move it off the board so we do not allow it.
      piece)))

(defmulti handle-command
  (fn [piece command]
    command))

(defmethod handle-command :down
  [piece command]
  (coordinate-change piece :y inc))

(defmethod handle-command :left
  [piece command]
  (coordinate-change piece :x dec))

(defmethod handle-command :right
  [piece command]
  (coordinate-change piece :x inc))

(defmethod handle-command :rotate
  [piece command]
  (let [num-positions (-> piece :piece :positions count)
        updated (update-in piece [:position-index] #(mod (inc %) num-positions))]
    (if (on-board? updated)
      updated
      piece)))
