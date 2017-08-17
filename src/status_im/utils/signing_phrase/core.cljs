(ns status-im.utils.signing-phrase.core
  (:require [status-im.utils.signing-phrase.dictionaries.en :as en]
            [status-im.utils.signing-phrase.dictionaries.ru :as ru]
            [clojure.string :as string]))

(def dictionaries
  {:en en/dictionary
   :ru ru/dictionary})

(defn pick-words [dictionary]
  (repeatedly 3 #(rand-nth dictionary)))

(defn generate []
  (string/join " " (pick-words en/dictionary)))


