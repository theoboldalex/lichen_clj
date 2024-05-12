(ns lichen-clj.core
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:gen-class))

(defn make-http-req [url]
  (with-open [reader (io/reader (io/input-stream (java.net.URL. url)))]
    (apply str (line-seq reader))))

(def ^:private licenses
  "https://api.github.com/licenses")

(defn get-available-licenses []
  (make-http-req licenses))

(defn get-license [l]
  (make-http-req (str licenses "/" (get l "key"))))

(defn -main
  "A simple OSS License Generator"
  [& args]
  (println
  (->>
   (get-available-licenses)
   (json/read-str)
   (first)
   (get-license))))