(ns falling-blocks.pieces
  (:require [falling-blocks.raster :as r]))


(def long-piece
  {:positions (mapv r/transpose
                    [
                     ;; TODO describe what these are
                     [[:red :red :red :red]]
                     
                     [[nil :red]
                      [nil :red]
                      [nil :red]
                      [nil :red]]
                     
                     [[:red :red :red :red]]
                     
                     [[nil nil :red]
                      [nil nil :red]
                      [nil nil :red]
                      [nil nil :red]]
                     ])})

(defn new-falling-piece
  [horizontal-pos]
  {:location [horizontal-pos 0]
   :piece long-piece
   :position-index 3})

(defn falling-piece->raster
  [{:keys [piece position-index]}]
  (get-in piece [:positions position-index]))

(defn apply-to-raster
  [raster falling-piece]
  (let [piece-raster (falling-piece->raster falling-piece)
        [x y] (:location falling-piece)]
    (r/raster-replace raster piece-raster x y)))

(defmulti handle-command
  (fn [falling-piece command]
    command))

(defmethod handle-command :down
  [falling-piece command]
  (update-in falling-piece [:location 1] inc))

(defmethod handle-command :left
  [falling-piece command]
  (update-in falling-piece [:location 0] dec))

(defmethod handle-command :right
  [falling-piece command]
  (update-in falling-piece [:location 0] inc))

(defmethod handle-command :rotate
  [falling-piece command]
  (let [num-positions (-> falling-piece :piece :positions count)]
    (update-in falling-piece [:position-index] #(mod (inc %) num-positions))))
