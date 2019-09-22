(ns yag.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def width 500)
(def height 500)

(defn inc-up-to [to x]
  (if (>= x to)
    0
    (inc x)))

(def inc-up-to-width (partial inc-up-to width))
(def inc-up-to-height (partial inc-up-to height))
(defn inc-coord
  [coord]
  (-> coord
    (update :x inc-up-to-width)
    (update :y inc-up-to-height)))

(defn random-star
  []
  {:x (q/random width)
   :y (q/random height)
   :color 255})

(defn update-starfield
  [state]
  (assoc state :stars 
    (map inc-coord (:stars state))))

(defn draw-star
  [star]
  (q/stroke-weight (q/random 1 3))
  (q/stroke (:color star))
  (q/point (:x star) (:y star)))

(defn draw-starfield
  [state]
  (q/background 0)
  (doseq [star (:stars state)]
    (draw-star star))
  state)

(defn update-ship
  [state]
  state)

(defn draw-ship
  [state]
  (let [ship (:ship state)]
    (q/no-fill)
    (q/stroke-weight (q/random 1 2))
    (q/stroke 0 255 0)
    (q/rect (:x ship) (:y ship) 16 16))
  state)

;FIXME: really buggy impl.
(defn move-ship
  [direction coord]
  (let [x (:x coord)]
    (if (and (<= x (- width 16)) (>= x 16))
      (assoc coord :x (if (= direction :left) (- x 16) (+ x 16)))
      (assoc coord :x (if (= direction :left) (+ x 16) (- x 16))))))
      
(defn left-ship
  [coord]
  (move-ship :left coord))

(defn right-ship
  [coord]
  (move-ship :right coord))

(defn draw-debug
  [state]
  (q/fill 255)
  (let [font (:debug (:font state))
        ship (:ship state)]
    (q/text-font font)
    (q/text (str "ship:" (:x ship) " " (:y ship)) 20 50))
  state)

(defn on-key-down
  [state event]
  (case (:key event)
    (:h :left) (update-in state [:ship] left-ship)
    (:l :right) (update-in state [:ship] right-ship)
    state))

(defn on-key-up
  [state event]
  state)

(defn setup
  []
  (q/frame-rate 60)
  (q/color-mode :rgb)
  {:font { :debug (q/create-font "Arial" 16)}
   :stars (take 100 (repeatedly random-star))
   :ship {:x (- (/ width 2) 16)
          :y (-  height (/ height 6))
          :color 255}})

(defn update-state
  [state]
  (->> state
     update-starfield
     update-ship))

(defn draw-state
  [state]
  (->> state
     draw-starfield
     draw-ship
     draw-debug))

(declare yag)

(q/defsketch yag
  :title "YAG"
  :size [width height]
  :setup setup
  :update update-state
  :draw draw-state
  :key-pressed on-key-down
  :key-released on-key-up
  :features [:keep-on-top]
  :middleware [m/fun-mode])
