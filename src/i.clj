(ns i
  "Helper for inspecting clojure values produced at runtime. The two main functions are save and 
  inspect. Save saves the value associated with a symbol for later inspection. Inspect returns a
  previously saved value.")

(def inspected
  "A map of inspected symbols to values"
  (atom {}))

(defn save*
  [sym value]
  (swap! inspected #(assoc % sym value)))

(defmacro save
  "Saves a value associated with the symbol used to represent it in code."
  [v]
  `(save* '~v ~v))

(defn inspect*
  [v]
  (get @inspected v))

(defmacro inspect
  "Returns a previously saved value."
  [v]
  `(inspect* '~v))

