(ns falling-blocks.board
  (:require [falling-blocks.raster :as r]
            [falling-blocks.pieces :as p]
            [clojure.set :as set]))

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

(defn validate-falling-piece
  "Validates that the falling piece on the board. Makes sure that any part of the piece in it's 
  current position does not overlap other pieces"
  [board falling-piece]
  ;; TODO validate it's still on the board (left, right, bottom)
  
  (let [fp-raster (p/falling-piece->raster falling-piece)
        {[x y] :location} falling-piece
        matrix (-> board :matrix-atom deref)
        covered-section (r/raster-subset 
                          matrix x y (r/raster-width fp-raster) (r/raster-height fp-raster))
        background-color (:background-color board)
        matrix-occupied-pos (set (r/locations-matching-value 
                                  covered-section #(not= % background-color)))
        piece-occupied-pos (set (r/locations-matching-value fp-raster some?))]
    (some? (seq (set/intersection matrix-occupied-pos piece-occupied-pos)))))

(comment
  
  (r/print-raster (-> user/system :board :matrix-atom deref))
  (r/print-raster (-> user/system :board :falling-piece-atom deref p/falling-piece->raster))
  
  
  (->> (p/falling-piece->raster (-> user/system :board :falling-piece-atom deref))
       r/raster->location-value-sequence
       (remove #(= (:value %) nil))
       (map #(vector (:x %) (:y %)))
       set)
  
  
  
  )


(defn create-board
  [options]
  (let [{:keys [board-width
                board-height
                background-color] :as options} (merge default-options options)
        falling-piece-atom (atom (p/new-falling-piece (/ board-width 2)))
        row (vec (repeat board-height background-color))
        board (map->Board
                (assoc options
                       :matrix-atom (atom (vec (repeat board-width row)))
                       :falling-piece-atom falling-piece-atom))]
    (set-validator! falling-piece-atom #(validate-falling-piece board %))
    board))

(def valid-commands
  #{:down :left :right :rotate})

(defn handle-command
  "Handles the command. Returns true or false depending on whether the command could be carried out."
  [board command]
  {:pre [(valid-commands command)]}
  (swap! (:falling-piece-atom board) #(p/handle-command % command)))