(ns memorystone.core
  (:use cljminecraft.core)
  (:require [memorystone.structures :as struc])
  ;(:import [org.bukkit.event Event Event$Type])
  )

(defn block-break [evt]
  (.sendMessage (.getPlayer evt) "You know. Breaking stuff should be illegal."))

(defn sign-change [evt]
  (.sendMessage (.getPlayer evt) "Now I've placed a sign and changed the text"))


(defn player-move [evt]
  (.sendMessage (.getPlayer evt) "Ok, no, really.. stop moving."))

(defn get-blocklistener []
  (cljminecraft.core/auto-proxy
   [org.bukkit.event.block.BlockListener] []
   (onBlockBreak [evt] (if (.isCancelled evt) nil (block-break evt)))
   (onSignChange [evt] (if (.isCancelled evt) nil (sign-change evt)))))

(defn enable-plugin [plugin]
    (def plugin* plugin)
    (def server* (.getServer plugin*))
    (def plugin-manager* (.getPluginManager server* ))
    (def plugin-desc* (.getDescription plugin*))

    (let [blocklistener (get-blocklistener)]
      (.registerEvent plugin-manager* (:BLOCK_BREAK event-types) blocklistener (:NORMAL event-priorities) plugin*)
      (.registerEvent plugin-manager* (:SIGN_CHANGE event-types) blocklistener (:NORMAL event-priorities) plugin*)
      )
  (log-info "Memory stone started"))

(defn disable-plugin [plugin]
  (log-info "Memory stone stopped"))