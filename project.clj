(defproject memorystone "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies []
  :dev-dependencies [[org.bukkit/bukkit "1.0.0-R1-SNAPSHOT"]
                     [clj-minecraft "1.0.0-SNAPSHOT"]
                     [org.clojure/clojure "1.3.0"]
                     [org.clojure/tools.logging "0.2.3"]]
  :repl-options [:init nil :caught clj-stacktrace.repl/pst+]
  :repositories {"spout-repo-snap" "http://repo.getspout.org/content/repositories/snapshots/"
                 "spout-repo-rel" "http://repo.getspout.org/content/repositories/releases/"})
