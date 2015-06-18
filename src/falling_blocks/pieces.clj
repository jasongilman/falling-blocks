(ns falling-blocks.pieces
  (:require [falling-blocks.draw :as d]))


(def long-piece
  {:positions (mapv d/transpose
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
    (d/raster-replace raster piece-raster x y)))