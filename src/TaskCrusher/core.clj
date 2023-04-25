(ns TaskCrusher.core
  (:require
   [clojure.pprint :as pp]
   [clojure.string :as string]
   [TaskCrusher.db :as db]))


(defn pretty-print-tasks []
  (pp/print-table (->> (db/select-all-tasks)
                       (map #(select-keys % [:id
                                             :description
                                             :project
                                             :status
                                             :tags])))))

(defn insert-task [args]
  (let [str_args (string/join args)]
    (println "Inserting the task" str_args)
    (db/insert-task {:description str_args})))

(defn delete-task [args]
  (let [id (Integer/parseInt (first args))]
    (println "Deleting the task" args)
    (db/delete-task-by-id id)))

(defn update-task [args]
  (let [id (Integer/parseInt (first args))
        str_args (string/join (rest args))]
    (db/update-task {:id id
                     :description str_args})))

(defn -main [& args]
  ;;(db/create-task-related-tables)
  (cond
    (= "add" (first args)) (insert-task (rest args))
    (= "list" (first args)) (print (pretty-print-tasks))
    (= "delete" (first args)) (delete-task (rest args))
    (= "update" (first args)) (update-task (rest args))
    :else (print "Invalid argument")))


(comment
  (def tasks (db/select-all-tasks))
  (insert-task "hello")
  )
