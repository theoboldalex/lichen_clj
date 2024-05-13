(ns lichen-clj.core
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.string :as string])
  (:gen-class))

(defn make-http-req [url]
  (with-open [reader (io/reader (io/input-stream (java.net.URL. url)))]
    (apply str (line-seq reader))))

(def prompt (slurp "welcome_prompt.txt"))

(def ^:private licenses_url
  "https://api.github.com/licenses")

(defn get-available-licenses []
  (json/read-str (make-http-req licenses_url)))

(def licenses (get-available-licenses))

(defn get-license [l]
  (json/read-str (make-http-req (str licenses_url "/" (get l "key")))))

(defn list-options []
  (->>
   licenses
  (map #(get % "name"))
  (map-indexed #(str (str (inc %1) ". ") %2))
  (string/join "\n")))

(defn get-choice []
  (print "\nSelect a license number: ")
  (flush)
  (let [choice (read-line)
        l (licenses (dec (Integer/parseInt choice)))]
    (println (str (get l "name") "! Nice!"))
    ((get-license l) "body")))

(defn write-license [body]
  (spit "LICENSE" body))

(defn -main
  "A simple OSS License Generator"
  [& args]
  (println prompt)
  (println (list-options))
 (let [choice (get-choice)]
   (write-license choice)))