(ns lichen-clj.core
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
 [clojure.string :as string])
  (:gen-class))

(defn make-http-req [url]
  (with-open [reader (io/reader (io/input-stream (java.net.URL. url)))]
    (apply str (line-seq reader))))

(def prompt (slurp "welcome_prompt.txt"))

(def ^:private licenses
  "https://api.github.com/licenses")

(defn get-available-licenses []
  (json/read-str (make-http-req licenses)))

(defn get-license [l]
  (make-http-req (str licenses "/" (get l "key"))))

(defn list-options []
  (->>
   (get-available-licenses)
   (map #(get % "name"))
   (map-indexed #(str (str (inc %1) ". ") %2))
   (string/join "\n")))

(defn -main
  "A simple OSS License Generator"
  [& args] 
  (println prompt)
 (println (list-options)))