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

(defn setup
  []
  (q/frame-rate 60)
  (q/color-mode :rgb)
  {:stars (take 100 (repeatedly random-star))
   :ship {:x (/ width 2)
          :y (/ height 2)}})

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
  (into state))


(defn update-state
  [state]
  (->> state
     update-starfield))

(defn draw-state
  [state]
  (->> state
     draw-starfield))

(declare yag)

(q/defsketch yag
  :title "YAG"
  :size [width height]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
