#!/usr/bin/env bb

(ns winswitcher
  (:require [clojure.string :as str]
            [clojure.java.shell :refer [sh]])
  (:gen-class))

(defn active-wins [class]
  (-> (sh "xdotool" "search" "--onlyvisible" "--desktop" "0"  "--class" class)
      :out
      (str/split-lines)
      (->> (map (fn [wid]
                  {:wid wid
                   :name (-> (clojure.java.shell/sh "xdotool" "getwindowname" wid) :out)})))))

(def most-recent-win (comp last active-wins))

(defn move-and-size! [{:keys [wid] :as _win} {:keys [x y width height] :as _pos}]
  (sh "wmctrl" "-ir" wid "-b" "remove,maximized_vert,maximized_horz")
  (sh "xdotool" "windowsize" wid (str width) (str height)
      "windowmove" wid (str x) (str y)
      "windowactivate" wid))

(defn connected-displays []
  (->> (sh "bash" "-c" "xrandr | grep -w connected")
       :out
       (str/split-lines)
       (map (fn [l] (let [[_ id coords] (re-find #"^([^\s]+)[^\d]+([\dx+-]+)" l)
                          [w h x y] (->> (re-find #"(\d+)x(\d+)([+-]\d+)([+-]\d+)" coords)
                                         (drop 1)
                                         (map #(Integer/parseInt %)))]
                      {:x1 x, :y1 y, :x2 (+ x w), :y2 (+ y h), :width w, :height h, :id id})))))

(defn pos-of-win [{:keys [wid]}]
  (let [[_ x y] (->> (sh "bash" "-c" (str "xwininfo -id " wid " | grep Absolute"))
                     :out
                     (re-find #"Absolute upper-left X:\s+(\d+)\n.+Absolute upper-left Y:\s+(\d+)"))]
    {:x (Integer/parseInt x)
     :y (Integer/parseInt y)}))

(defn shows-window [{:keys [x1 x2 y1 y2] :as _display} {win-x :x, win-y :y :as _win}]
  (and (<= x1 win-x x2)
       (<= y1 win-y y2)))

(defn parse-layout-definition [s]
  (let [parts (->> (str/split s #",")
                   (map (fn [l]
                          (let [[class parts] (str/split l #"\.")]
                            [class (Integer/parseInt parts)]))))]
    {:total-segments (->> parts (map second) (reduce +))
     :parts parts}))

(defn apply-layout [s]
  (let [top-panel-height 32
        {:keys [total-segments parts]} (parse-layout-definition s)
        displays (connected-displays)
        first-app (most-recent-win (->> parts ffirst))
        display-for-first-app (->> displays
                                   (filter #(shows-window % (pos-of-win first-app)))
                                   first)
        y1 (:y1 display-for-first-app)
        height (:height display-for-first-app)
        disp-width (:width display-for-first-app)]
    (loop [remaining-parts parts
           x (:x1 display-for-first-app)]
      (let [[part & rest-parts] remaining-parts]
        (when part
          (let [[class segments] part
                width (int (* disp-width (/ segments total-segments)))
                x2 (+ x width)
                app-window (most-recent-win class)]
            (move-and-size! app-window {:x x, :y y1, :width width, :height (- height top-panel-height 1)})
            (when-not (empty? rest-parts)
              (recur rest-parts x2))))))))

(defn -main [layout & _args]
  (apply-layout layout)

  (when-not *command-line-args*
    (System/exit 0)))

(when *command-line-args*
  (apply -main *command-line-args*))
