(ns memorystone.core
  (:require [cljminecraft.events :as ev])
  (:require [cljminecraft.player :as pl])
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.logging :as log]
            [cljminecraft.commands :as cmd]
            [cljminecraft.world :as w]
            [cljminecraft.items :as i])
  (:require [cljminecraft.files :as files]))

(defonce plugin (atom nil))
(defonce memorystones (atom #{}))
(defonce memorizedstones (atom {}))


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

(defn write-memory [player-name]
  (let [memory (get @memorizedstones player-name)]
    (files/write-json-file
     @plugin
     (str "memory-" player-name ".json")
     {:memory memory})))

(defn read-memory [player-name]
  (let [{:keys [memory]} (files/read-json-file @plugin (str "memory-" player-name ".json"))]
    (swap! memorizedstones update-in [player-name] (comp set concat) memory)))

;; Event Handling
(defn memorize-stone! [player {:keys [name] :as memstone}]
  (swap! memorizedstones update-in [player] (comp set conj) name)
  (write-memory player))

(defn unmemorize-stone! [player {:keys [name] :as memstone}]
  (swap! memorizedstones update-in [player] (comp set disj) name)
  (write-memory player))

(defn sign-change [ev]
  (let [[type name] (seq (.getLines ev))]
    (cond
     (not= "[MemoryStone]" type) nil
     (empty? name) (do (pl/send-msg ev "Please enter a name on the second line"))
     :else
     (do
       (swap! memorystones conj {:block (.getBlock ev) :name name})
       (write-memstones)
       {:msg "You placed a Memory Stone. Well done."}))))

(defn memstone-for-block [block]
  (first (filter #(.equals block (:block %)) @memorystones)))

(defn sign-break [ev]
  (when-let [memstone (memstone-for-block (.getBlock ev))]
    (swap! memorystones disj memstone)
    (doseq [[player memorized] @memorizedstones]
      (if (contains? memorized (:name memstone))
        (unmemorize-stone! player memstone)))
    (write-memstones)
    {:msg "You broke a Memory Stone! oh dear."}))

(defn player-interact [ev]
  (if (and
       (= (.getAction ev) (get ev/actions :left_click_block))
       (i/is-block? (.getClickedBlock ev) :wall_sign :sign_post))
    (if-let [memstone (memstone-for-block (.getClickedBlock ev))]
      (do
        (memorize-stone! (.getName (pl/get-player ev)) memstone)
        {:msg (format "Memorized %s" (:name memstone))}))))

(defn player-login [ev]
  (read-memory (.getName (pl/get-player ev))))

(defn events
  []
  [(ev/event "block.sign-change" #'sign-change)
   (ev/event "block.block-break" #'sign-break)
   (ev/event "player.player-interact" #'player-interact)
   (ev/event "player.player-login" #'player-login)])

;; Command Handling
(defn get-memorized-stones [named]
  (let [stones (get @memorizedstones (.getName named) #{})]
    (filter #(contains? stones (:name %)) @memorystones)))

(defmethod cmd/convert-type :memorystone [sender type arg]
  (log/info "Converting %s" (get @memorizedstones (.getName sender) #{}))
  (first (filter #(= (.toLowerCase (:name %)) (.toLowerCase arg)) (get-memorized-stones sender))))

(defmethod cmd/param-type-tabcomplete :memorystone [sender type arg]
  (let [lower (.toLowerCase arg)]
    (filter #(.startsWith % lower) (map #(.toLowerCase (:name %)) (get-memorized-stones sender)))))

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


