(ns memorystone.core
  (:require [cljminecraft.events :as ev])
  (:require [cljminecraft.player :as pl])
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.logging :as log]
            [cljminecraft.commands :as cmd]
            [cljminecraft.world :as w])
  (:require [cljminecraft.files :as files]))

(defonce plugin (atom nil))
(defonce memorystones (atom #{}))

;; Persistence handling
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

(defn write-memstones []
  (files/write-json-file @plugin
   "stones.json"
   {:stones (map memstone-to-file @memorystones)}))

(defn read-memstones []
  (let [{:keys [stones]} (files/read-json-file @plugin "stones.json")]
    (reset! memorystones (set (map memstone-from-file stones)))))

;; Event Handling
(defn sign-change [ev]
  (let [[type name] (seq (.getLines ev))]
    (cond
     (not= "[MemoryStone]" type) nil
     (empty? name) (do (pl/send-msg ev "Please enter a name on the second line"))
     :else
     (do
       (swap! memorystones conj {:block (.getBlock ev) :name name})
       (write-memstones)
       (pl/send-msg ev "You placed a Memory Stone. Well done.")))))

(defn memstone-for-block [block]
  (first (filter #(.equals block (:block %)) @memorystones)))

(defn sign-break [ev]
  (when-let [memstone (memstone-for-block (.getBlock ev))]
    (pl/send-msg ev "You broke a Memory Stone! oh dear.")
    (swap! memorystones disj memstone)
    (write-memstones)))

(defn events
  []
  [(ev/event "block.sign-change" #'sign-change)
   (ev/event "block.block-break" #'sign-break)])

;; Command Handling
(defmethod cmd/convert-type :memorystone [sender type arg]
  (first (filter #(= (.toLowerCase (:name %)) (.toLowerCase arg)) @memorystones)))

(defmethod cmd/param-type-tabcomplete :memorystone [sender type arg]
  (let [lower (.toLowerCase arg)]
    (filter #(.startsWith % lower) (map #(.toLowerCase (:name %)) @memorystones))))

(defn go-command [sender memorystone]
  (when memorystone
    (log/info "Teleporting %s to %s " sender memorystone)
    (.teleport sender (.getLocation (w/facing-block (:block memorystone))))
    {:msg "Teleported"}))


;; Plugin lifecycle
(defn start
  [plugin-instance]
  (log/info "%s" "in start memorystone")
  (reset! plugin plugin-instance)
  (ev/register-eventlist @plugin (events))
  (cmd/register-command @plugin "go" #'go-command :memorystone)
  (read-memstones))

(defn stop
  [plugin]
  (log/info "%s" "in stop memorystone")
  (write-memstones))


