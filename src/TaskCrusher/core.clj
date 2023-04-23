(ns TaskCrusher.core
  (:require [TaskCrusher.db :as db]
            [clojure.string :as string]))


(defn pretty-print-tasks []
  (->> (db/select-all-tasks)
       (map #(:description %))
       (run! println)))

(defn insert-task [args]
  (let [str_args (string/join args)]
    (println "Inserting the task" str_args)
    (db/insert-task {:description str_args})))

(defn delete-task [args]
  (let [id (Integer/parseInt (first args))]
    (println "Deleting the task" args)
    (db/delete-task-by-id id)))

(defn -main [& args]
  ;;(db/create-task-related-tables)
  (cond
    (= "add" (first args)) (insert-task (rest args))
    (= "list" (first args)) (print (pretty-print-tasks))
    (= "delete" (first args)) (delete-task (rest args))
    (= "update" (first args)) (print (pretty-print-tasks))
    :else (print "Invalid argument")))


(comment
  (def tasks (db/select-all-tasks))
  (insert-task "hello")
  )
