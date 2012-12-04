(ns memorystone.core
  (:require [cljminecraft.events :as ev])
  (:require [cljminecraft.player :as pl])
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.logging :as log])
  (:require [cljminecraft.files :as files]))

(defonce memorystones (atom #{}))

(defn sign-change [ev]
  (let [[type name] (seq (.getLines ev))]
    (cond
     (not= "[MemoryStone]" type) nil
     (empty? name) (do (pl/send-msg ev "Please enter a name on the second line"))
     :else
     (do
       (swap! memorystones conj {:block (.getBlock ev) :name name})
       (pl/send-msg ev "You placed a Memory Stone. Well done.")))))

(defn memstone-for-block [block]
  (first (filter #(.equals block (:block %)) @memorystones)))

(defn sign-break [ev]
  (when-let [memstone (memstone-for-block (.getBlock ev))]
    (pl/send-msg ev "You broke a Memory Stone! oh dear.")
    (swap! memorystones disj memstone)))

(defn memstone-to-file [{:keys [block name] :as memstone}]
  (let [{:keys [x y z] :as location} (bean (.getLocation block))]
    {:world-uuid (str (.. block getWorld getUID))
     :location [x y z]
     :name name}))

(defn memstone-from-file [{:keys [world-uuid location name] :as memstone}]
  (let [world (bk/world-by-uuid world-uuid)
        [x y z] location
        block (.getBlockAt world x y z)]
    {:block block
     :name name}))

(defn write-memstones [ev]
  (files/write-json-file
   "stones.json"
   {:stones (map memstone-to-file @memorystones)}))

(defn read-memstones []
  (let [{:keys [stones]} (files/read-json-file "stones.json")]
    (reset! memorystones (set (map memstone-from-file stones)))))

(defn events
  []
  [(ev/event block.sign-change #'sign-change)
   (ev/event block.block-break #'sign-break)])

(defn start2 [plugin]
  (ev/register-eventlist plugin (events)))


(defn start
  [pluginInstance]
  (. pluginInstance info "in start memorystone.")
  (log/info "%s" "in start memorystone")
  (start2 pluginInstance)
  )

(defn stop
  [pluginInstance]
  (. pluginInstance info "in stop memorystone.")
  (log/info "%s" "in stop memorystone")
  ;TODO: deregister events?
  )

