(defproject clj-memorystone "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [
                     [clj-minecraft "1.0.0-SNAPSHOT"]
                     [org.clojure/clojure "1.4.0"]
                     [org.clojure/tools.logging "0.2.3"]
                     [org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT"]
                     ]
  
  :warn-on-reflection true
  
  :repl-options [:init nil :caught clj-stacktrace.repl/pst+]
  
  :repositories [
                 ;"spout-repo-snap" "http://repo.getspout.org/content/repositories/snapshots/"
                 ;"spout-repo-rel" "http://repo.getspout.org/content/repositories/releases/"
                 ;"org.bukkit" "Bukkit" "bukkit" {:url "http://repo.bukkit.org/content/groups/public/"}
                 ["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]
                 ]
  )