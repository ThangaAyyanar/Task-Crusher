(ns TaskCrusher.core
  (:require [TaskCrusher.db :as db]
            [clojure.string :as string]))


(defn pretty-print-tasks []
  (->> (db/select-all-tasks)
       (map #(:description %))
       (run! println)))

(defn -main [& args]
  ;;(create-task-related-tables)
  (cond
    (= "add" (first args)) (print (rest args))
    (= "list" (first args)) (print (pretty-print-tasks))
    :else (print "Invalid argument")))


(comment
  (def tasks (db/select-all-tasks))
  )
