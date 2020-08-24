;;; You won't need these requires if you use babashka.
(require '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[clojure.java.shell :as shell])


(defn parse-display-header-line [line]
  (let [[id state & fields] (str/split line #" ")]
    {:id id
     :is-connected (= "connected" state)
     :is-primary (= "primary" (first fields))}))


(defn parse-display-mode-line [line]
  (let [[shape-str framerate-strs] (-> line str/trim (str/split #" +" 2))
        is-interlaced (str/ends-with? shape-str "i")
        shape-str (if is-interlaced (subs shape-str 0 (dec (count shape-str))) shape-str)]
    {:shape (mapv edn/read-string (str/split shape-str #"x"))
     :is-interlaced is-interlaced
     :is-current (boolean (some #{\*} framerate-strs))
     :is-preffered (boolean (some #{\+} framerate-strs))}))


(defn parse-display-string [string]
  (let [[header & modes] (str/split-lines string)
        header-info (parse-display-header-line header)
        modes-info (mapv parse-display-mode-line modes)
        selected-mode (first (concat (filter :is-current modes-info)
                                     (filter :is-preffered modes-info)))]
    (cond-> header-info
      (seq selected-mode) (assoc :shape (:shape selected-mode)))))


(defn parse-xrandr [xrandr-output]
  (let [matching-strings (str/split xrandr-output #"\n(?!   )")
        display-strings (filter (complement #(str/starts-with? % "Screen")) matching-strings)]
    (mapv parse-display-string display-strings)))
