(ns falling-blocks.raster
  "Defines functions for manipulating rasters. A raster is essentially a matrix. It's is a vector of 
  columns. Each column is a vector of keywords representing the colors in those locations.")

(defn raster-height
  [r]
  (-> r first count))

(defn raster-width
  [r]
  (count r))

(defn valid-x?
  [r x]
  (and (>= x 0)
       (< x (raster-width r))))

(defn valid-y?
  [r y]
  (and (>= y 0)
       (< y (raster-height r))))

(defmacro pre-logger
  "Helper macro for function pre checks. It prints out the values of the variables used in the pre check."
  [body]
  (let [vars (keys &env)
        println-args (reduce into [] (for [v vars]
                                       [(str (name v) "=") v]))]
    `(let [result# ~body]
       (when-not result#
         (println "pre fail:" ~@println-args))
       result#)))

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
  "Replaces elements of raster 1 with raster 2 starting from the specified x and y offsets."
  ([r1 r2]
   (raster-replace r1 r2 0 0))
  ([r1 r2 x-offset y-offset]
   (vec-replace-if r1 r2 x-offset 
                   (fn [col1 col2]
                     (vec-replace-if col1 col2 y-offset
                                     #(or %2 %1))))))

(defn raster-subset
  "Returns a subset of a given raster."
  [raster x-offset y-offset width height]
  {:pre [(pre-logger (valid-x? raster x-offset))
         (pre-logger (valid-x? raster (dec (+ x-offset width))))
         (pre-logger (valid-y? raster y-offset))
         (pre-logger (valid-y? raster (dec (+ y-offset height))))]}
  
  (mapv #(subvec % y-offset (+ y-offset height))
        (subvec raster x-offset (+ x-offset width))))

(defn transpose
  "Transposes a given raster. If you pass in a normal raster of vector of columns it will become a
  vector of rows or vice versa."
  [m]
  (apply mapv vector m))

(defn print-raster
  "Prints out a raster. Helps with debugging"
  [r]
  (println)
  (doseq [row (transpose r)]
    (println (pr-str row))))

(defn raster->location-value-sequence
  "Converts a raster into a sequence of maps containing :x, :y, and :value. Returned in order left 
  to right and top to bottom"
  [raster]
  (for [[x column] (map-indexed vector raster)
        [y value] (map-indexed vector column)]
    {:x x :y y :value value}))

(defn locations-matching-value
  "Returns a sequence of x,y tuplies of locations in the tuple for which the value function returns true"
  [raster test-fn]
  (->> raster
       raster->location-value-sequence
       (filter #(test-fn (:value %)))
       (map #(vector (:x %) (:y %)))))


(comment
  
  
  (def r3 (transpose [[ 1  2  3  4]
                      [ 5  6  7  8]
                      [ 9 10 11 12]
                      [13 14 15 16]]))
  
  (print-raster r3)
  
  (raster->location-value-sequence r3)
  
  (print-raster (raster-subset r3 2 2 1 2))
  
  (def r4 (transpose [[:a :b :c :d]
                      [nil :f :g :h]
                      [:i :j nil :l]
                      [:m :n :o :p]]))
  
  (def r5  (transpose [[:a :b :c]
                       [:d :e :f]]))
  
  
  (raster-replace r3 r4)
  
  (= (raster-replace r3 r5)
     (transpose [[:a :b :c 4] 
                 [:d :e :f 8] 
                 [9 10 11 12] 
                 [13 14 15 16]]))
  
  (= (raster-replace r3 r5 1 0)
     (transpose [[1 :a :b :c] 
                 [5 :d :e :f] 
                 [9 10 11 12] 
                 [13 14 15 16]]))
  
  (= (raster-replace r3 r5 1 2)
     (transpose [[ 1 2 3 4] 
                 [ 5 6 7 8] 
                 [ 9 :a :b :c] 
                 [13 :d :e :f]]))
  
  )  
