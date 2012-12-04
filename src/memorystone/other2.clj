(ns memorystone.other2)

(defn test [pluginInstance a b c]
  (. pluginInstance info (str a b c))
  )