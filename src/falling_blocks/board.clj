(ns falling-blocks.board
  "The board represents the playable area of the game where pieces fall."
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
   
   ;; A keyword representing the background colore of the board
   background-color
   
   ;; An atom containing the current state of the board as a matrix which consists of a vector of 
   ;; vectors. The outer vector contains columns. Each column is a vector of symbols of colors.
   matrix-atom
   
   ;; An atom containing the state of the current falling piece on the board.
   ;; falling-block.pieces namespace for a description of a piece
   falling-piece-atom
   ]
  )

(defn merge-falling-piece
  [{:keys [matrix-atom falling-piece-atom board-width]}]
  (swap! matrix-atom #(p/apply-to-raster % @falling-piece-atom))
  (try 
    (reset! falling-piece-atom (p/new-falling-piece (/ board-width 2)))
    true
    (catch IllegalStateException _
      ;; Game over. The board is full.
      false)))

(defn falling-piece-validator
  "Checks if the falling piece is in a good position ie. does not overlap any other pieces."
  [board falling-piece]
  (let [fp-raster (p/to-raster falling-piece)
        {[x y] :location} falling-piece
        matrix (-> board :matrix-atom deref)
        covered-section (r/raster-subset 
                          matrix x y (r/raster-width fp-raster) (r/raster-height fp-raster))
        background-color (:background-color board)
        matrix-occupied-pos (set (r/locations-matching-value 
                                  covered-section #(not= % background-color)))
        piece-occupied-pos (set (r/locations-matching-value fp-raster some?))]
    (nil? (seq (set/intersection matrix-occupied-pos piece-occupied-pos)))))

(defn create-board
  "Creates a new board with the given options"
  ([]
   (create-board nil))
  ([options]
   (let [{:keys [board-width
                 board-height
                 background-color] :as options} (merge default-options options)
         falling-piece-atom (atom (p/new-falling-piece (/ board-width 2)))
         row (vec (repeat board-height background-color))
         board (map->Board
                 (assoc options
                        :matrix-atom (atom (vec (repeat board-width row)))
                        :falling-piece-atom falling-piece-atom))]
     
     (when-not (falling-piece-validator board (deref falling-piece-atom))
       (throw (Exception. "Invalid initial state")))
     
     ;; falling-piece-validator is used as the validator of the falling piece atom. It will
     ;; make sure it doesn't overlap any other pieces on the board.
     (set-validator! falling-piece-atom #(falling-piece-validator board %))
     
     board)))

(defn board-raster
  "Returns a board raster showing what should be displayed with the current falling piece"
  [{:keys [matrix-atom falling-piece-atom]}]
  (let [matrix (deref matrix-atom)
        falling-piece (deref falling-piece-atom)]
    (p/apply-to-raster matrix falling-piece)))

(def valid-commands
  "Valid movement commands for a piece"
  #{:down :left :right :rotate})

(defn handle-command
  "Handles the command. Returns true or false depending on whether the command could be carried out."
  [board command]
  {:pre [(valid-commands command)]}
  (try
    (let [falling-piece-atom (:falling-piece-atom board)
          before-state @falling-piece-atom
          after-state (swap! falling-piece-atom #(p/handle-command board % command))]
      ;; Return true if there was an actual change
      (not= before-state after-state))
    (catch IllegalStateException _
      ;; The validator was triggered for the command which means it would overlap another peice.
      false)))


