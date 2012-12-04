(defproject memorystone "2.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [
                     [clj-minecraft "1.0.1-SNAPSHOT"]
                     ;make sure any required projects here either are already in clj-minecraft uberjar or
                     ;just make this an uberjar; or find a way to add them to ../lib in bukkit
                     [org.clojure/clojure "1.4.0"]
                     [org.clojure/tools.logging "0.2.3"]
                     [org.bukkit/bukkit "1.4.5-R0.3-SNAPSHOT"]
                     ]
  
  :repl-options [:init nil :caught clj-stacktrace.repl/pst+]
  
  :repositories [
                 ;"spout-repo-snap" "http://repo.getspout.org/content/repositories/snapshots/"
                 ;"spout-repo-rel" "http://repo.getspout.org/content/repositories/releases/"
                 ;"org.bukkit" "Bukkit" "bukkit" {:url "http://repo.bukkit.org/content/groups/public/"}
                 ["bukkit.snapshots" "http://repo.bukkit.org/content/repositories/snapshots"]
                 ]
  )