(ns falling-blocks.draw
  "Defines functions for drawing.
  TODO
  A raster is a vector of columns
  
  ")
[]
(defn- vec-replace-if
  "Conditionally replaces elements of v1 with elements from v2. It starts from the specified offset
  and uses the choose-fn to determine what value to use in v1."
  [v1 v2 offset choose-fn]
  {:pre [(>= offset 0)
         (<= (+ offset (count v2)) (count v1))]}
  (let [v1-begin (subvec v1 0 offset)
        new-middle (mapv choose-fn (subvec v1 offset) v2)
        v1-end (subvec v1 (+ (count v2) offset))]
    (into (into v1-begin new-middle) v1-end)))

(defn raster-replace
  "TODO
  Replaces elements of raster 1 with raster 2 starting from the specified x and y offsets
  y offset from the top
  x offset from the left"
  ([r1 r2]
   (raster-replace r1 r2 0 0))
  ([r1 r2 x-offset y-offset]
   (vec-replace-if r1 r2 x-offset 
                   (fn [col1 col2]
                     (vec-replace-if col1 col2 y-offset
                                     #(or %2 %1))))))

(defn transpose 
  [m]
  (apply mapv vector m))

(defn print-raster
  "TODO helps with debugging"
  [r]
  (println)
  (doseq [row (transpose r)]
    (println (pr-str row))))


(comment

  
  (def r3 [[ 1  2  3  4]
           [ 5  6  7  8]
           [ 9 10 11 12]
           [13 14 15 16]])
  
  (def r4 [[:a :b :c :d]
           [nil :f :g :h]
           [:i :j nil :l]
           [:m :n :o :p]])
  
  (def r5  [[:a :b :c]
            [:d :e :f]])
  
 
  (raster-replace r3 r4)
  
  (= (raster-replace r3 r5)
     [[:a :b :c 4] 
      [:d :e :f 8] 
      [9 10 11 12] 
      [13 14 15 16]])
  
  (= (raster-replace r3 r5 1 0)
     [[1 :a :b :c] 
      [5 :d :e :f] 
      [9 10 11 12] 
      [13 14 15 16]])
  
  (= (raster-replace r3 r5 1 2)
     [[ 1 2 3 4] 
      [ 5 6 7 8] 
      [ 9 :a :b :c] 
      [13 :d :e :f]])
 
)  
