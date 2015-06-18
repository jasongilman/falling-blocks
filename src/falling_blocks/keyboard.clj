(ns falling-blocks.keyboard
  (:require [clojure.core.async :as a])
  (:import java.awt.event.KeyListener
           java.awt.event.KeyEvent
           javax.swing.event.MouseInputListener))

(defn- ultimate-parent
  "TODO"
  [swing-panel]
  (when swing-panel
    (or (ultimate-parent (.getParent swing-panel)) 
        swing-panel)))

(defn create-key-channel
  "Creates a key channel with a dropping buffer that will contain strings with the keys pressed"
  [swing-panel]
  (let [ch (a/chan (a/sliding-buffer 10))
        kl (reify KeyListener
             (keyReleased [this e] )
             (keyTyped [this e] )
             (keyPressed
               [this e] 
               (a/>!! ch (KeyEvent/getKeyText (.getKeyCode e)))))]
    (.addKeyListener (ultimate-parent swing-panel) kl)
    ch))