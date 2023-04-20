(ns core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(def taskLimit 2)
;; Specs
(def db-spec {:dbtype "sqlite"
              :dbname (io/resource "sample.db")
              :builder-fn rs/as-unqualified-lower-maps ;; remove table name from result
              })

(def ds (jdbc/get-datasource db-spec))

;; Utils

(defn current-date []
  (new java.util.Date))

;; Table Creation Scripts

(defn create-task-related-tables []

    (jdbc/execute! ds ["CREATE TABLE tasks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        uuid TEXT,
        description TEXT,
        entry DATETIME,
        start DATETIME,
        end DATETIME,
        due DATETIME,
        wait DATETIME,
        scheduled DATETIME,
        modified DATETIME,
        status TEXT,
        tags TEXT,
        priority INTEGER,
        project TEXT
    )"])

    (jdbc/execute! ds ["CREATE TABLE annotations (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            description TEXT,
            entry DATETIME,
            uuid TEXT,
            task INTEGER,
            FOREIGN KEY (task) REFERENCES tasks(id)
        )"]))

(defn insert-task [args]
  ;; Sample args will be like this
  ;; {:uuid "some uuid" :description "Hello world" :entry datetime :status pending :project Inbox}
  (let [uuid (java.util.UUID/randomUUID)
        description (:description args "hello")
        entry (:entry args (current-date))
        status (:status args "pending")
        priority (:priority args 1)
        project (:project args "Inbox")]

    (jdbc/execute! ds ["INSERT INTO tasks (uuid, description, entry, status, priority, project) VALUES (?,?,?,?,?,?)"
                       uuid description entry status priority project]))
  )

(defn update-task-description [uuid description]
  (jdbc/execute! ds ["UPDATE tasks SET description = ?, modified = ? WHERE uuid = ?" description (current-date) uuid]))

(defn update-task-status [uuid status]
  (jdbc/execute! ds ["UPDATE tasks SET status = ?, modified = ? WHERE uuid = ?" status (current-date) uuid]))

(defn update-task-priority [uuid priority]
  (jdbc/execute! ds ["UPDATE tasks SET priority = ?, modified = ? WHERE uuid = ?" priority (current-date) uuid]))

(defn update-task-project [uuid project]
  (jdbc/execute! ds ["UPDATE tasks SET project = ?, modified = ? WHERE uuid = ?" project (current-date) uuid]))

(defn update-task [args]
  ;; Sample args will be like this
  ;; {:uuid "some uuid" :description "Hello world" :entry datetime :status pending :project Inbox}

  (if (empty? (:uuid args))
    (println "Need UUID to update the task")
    (let [uuid (:uuid args)]
        (cond
            (:description args) (update-task-description uuid (:description args))
            (:status args) (update-task-status uuid (:status args))
            (:prioriy args) (update-task-priority uuid (:priority args))
            (:project args) (update-task-project uuid (:project args))
            ))))

 
(defn select-all-tasks
  ([] (select-all-tasks taskLimit))
  ([limit]
    (jdbc/execute! ds ["SELECT * FROM tasks LIMIT ?" limit] {:builder-fn rs/as-unqualified-lower-maps ;; remove table name from result
              })))

(defn pretty-print-tasks []
  (println (map #(str (:description %) "\n") (select-all-tasks))))

(defn delete-task-by-id [id]
  (println (str "Deleting " id "from the table task"))
  (jdbc/execute! ds ["DELETE FROM tasks WHERE id = ?" id]))

(defn delete-task-by-uuid [uuid]
  (println (str "Deleting " uuid "from the table task"))
  (jdbc/execute! ds ["DELETE FROM tasks WHERE uuid = ?" uuid]))

(defn delete-task [args]
  (cond
    (:id args) (delete-task-by-id (:id args))
    (:uuid args) (delete-task-by-id (:uuid args))
    :else (println "Not a valid argument")))


(defn -main [& args]
  ;;(create-task-related-tables)
  (cond
    (= "add" (first args)) (print (rest args))
    (= "list" (first args)) (print (pretty-print-tasks))
    :else (print "Invalid argument")))

(comment

  (update-task {:uuid "076e3ab1-6403-45e2-8c12-93aeac462325"
                :description "Clean up the code for task table"})

  (delete-task {:id 1})

  (insert-task {:description "insert item for task crusader in clojure"})
  (println (select-all-tasks))

  (defn query-db [query & params]
    (jdbc/execute! db-spec [query] params))

  (query-db "")

  (defn create-table []
    (jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT, email TEXT)"]))

  (create-table)

  (jdbc/execute! ds ["INSERT INTO tasks (uuid, description, entry, status, priority, project) VALUES
  ('12345678-1234-5678-1234-567812345678', 'Task 1', '2023-04-08 10:00:00', 'pending', 1, 'Project A'),
  ('12345678-1234-5678-1234-567812345679', 'Task 2', '2023-04-08 11:00:00', 'pending', 2, 'Project A'),
  ('12345678-1234-5678-1234-567812345680', 'Task 3', '2023-04-08 12:00:00', 'pending', 3, 'Project B'),
  ('12345678-1234-5678-1234-567812345681', 'Task 4', '2023-04-08 13:00:00', 'pending', 4, 'Project C'),
  ('12345678-1234-5678-1234-567812345682', 'Task 5', '2023-04-08 14:00:00', 'pending', 5, 'Project D')"])
  
 )
