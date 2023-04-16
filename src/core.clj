(ns core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [clojure.java.io :as io]))

;; Specs
(def db-spec {:dbtype "sqlite"
              :dbname (io/resource "sample.db")
              :builder-fn rs/as-unqualified-lower-maps ;; remove table name from result
              })
(def ds (jdbc/get-datasource db-spec))

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
        entry (:entry args (new java.util.Date))
        status (:status args "pending")
        priority (:priority args 1)
        project (:project args "Inbox")]

    (jdbc/execute! ds ["INSERT INTO tasks (uuid, description, entry, status, priority, project) VALUES (?,?,?,?,?,?)"
                       uuid description entry status priority project]))
  )

(defn select-all-tasks
  ([] (select-all-tasks 2))
  ([limit]
    (jdbc/execute! ds ["SELECT * FROM tasks LIMIT ?" limit])))

(defn -main [& args]
  (create-task-related-tables)
  )

(comment

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
