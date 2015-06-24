(ns falling-blocks.pieces
  "Contains functions for creating data representing the 'pieces' on the board."
  (:require [falling-blocks.raster :as r]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars describing the pieces. There will be a var per peice type here.
;; They will each consist of a map of their positions. The positions are a vector containing different
;; rasters of the rotated positions. The transpose function is used so that the pieces can be entered
;; in a way that is easily typed in a text file.

(def long-piece
  "Defines the long 4 block piece."
  {:positions (mapv r/transpose
                    [
                     ;; There are two different positions for the long piece
                     [[:red :red :red :red]]
                     
                     [[:red]
                      [:red]
                      [:red]
                      [:red]]
                     ])})

(def L-piece
  {:positions 
   (mapv r/transpose
         [ 
          
          [[:dark-blue :dark-blue :dark-blue]
           [nil         nil       :dark-blue]]
          
          [[nil :dark-blue]
           [nil :dark-blue]
           [:dark-blue :dark-blue]]
          
          [[:dark-blue nil nil]
           [:dark-blue :dark-blue :dark-blue]]
          
          [[:dark-blue :dark-blue]
           [:dark-blue nil]
           [:dark-blue nil]]
          
          ])})

(comment
  (r/print-raster (-> L-piece :positions first))
  
  )

(def square-piece 
  {:positions [
               
               [[:yellow :yellow]
                [:yellow :yellow]]
               
               ]})


;; TODO add more piece types


;; TODO make this create a random piece
(defn new-falling-piece
  "Creates a new falling piece. A falling piece has a piece and information about its location and
  rotation on the board."
  [horizontal-pos]
  {:location [horizontal-pos 0]
   :piece L-piece
   :position-index 0})

(defn to-raster
  "Returns a raster of the falling piece in it's current rotation."
  [{:keys [piece position-index]}]
  (get-in piece [:positions position-index]))

(defn apply-to-raster
  "Takes a raster and a falling piece to apply to it. It 'draws' the falling piece on top of the 
  raster."
  [raster fp]
  (let [fp-raster (to-raster fp)
        [x y] (:location fp)]
    (r/raster-replace raster fp-raster x y)))

(defn- on-board?
  "Returns true if the piece currently fits on the board."
  [{:keys [board-width board-height]} {[x y] :location :as piece}]
  (let [raster (to-raster piece)
        pos-height (r/raster-height raster)
        pos-width (r/raster-width raster)]
    (and (>= x 0)
         (<= (+ x pos-width) board-width)
         (>= y 0)
         (<= (+ y pos-height) board-height))))

(defn- coordinate-change
  "Updates the pieces location coordinate only if it can still stay on the board."
  [board piece coordinate f]
  (let [location-index (get {:x 0 :y 1} coordinate)
        updated (update-in piece [:location location-index] f)]
    (if (on-board? board updated)
      updated
      ;; The change would move it off the board so we do not allow it.
      piece)))

(defmulti handle-command
  "Handles a command to move a piece on the board."
  (fn [board piece command]
    command))

(defmethod handle-command :down
  [board piece command]
  (coordinate-change board piece :y inc))

(defmethod handle-command :left
  [board piece command]
  (coordinate-change board piece :x dec))

(defmethod handle-command :right
  [board piece command]
  (coordinate-change board piece :x inc))

(defmethod handle-command :rotate
  [board piece command]
  (let [num-positions (-> piece :piece :positions count)
        updated (update-in piece [:position-index] #(mod (inc %) num-positions))]
    (if (on-board? board updated)
      updated
      piece)))
