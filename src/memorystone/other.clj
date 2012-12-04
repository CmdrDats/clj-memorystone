(ns memorystone.other)

(defn test2 [pluginInstance a b c]
  (. pluginInstance info (str a b c))
  )