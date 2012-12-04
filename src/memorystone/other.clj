(ns memorystone.other)

(defn test [pluginInstance a b c]
  (. pluginInstance info (str a b c))
  )