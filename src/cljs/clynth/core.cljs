(ns clynth.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [clynth.events :as events]
   [clynth.views :as views]
   [clynth.config :as config]
   [cljs-bach.synthesis :as b]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))

(defonce context (b/audio-context))

(defn ping [freq]
  (b/connect->
    (b/square freq)
    (b/percussive 0.01 0.4)
    (b/gain 0.1)))

(defn play-ping []
  (-> (ping 440)
      (b/connect-> b/destination)
      (b/run-with context (b/current-time context) 1.0)))

(js/setInterval play-ping 1000)