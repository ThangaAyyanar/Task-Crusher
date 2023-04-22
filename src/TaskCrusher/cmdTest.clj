
(ns cmdTest
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.main :as main]))


;; command line args
;; (def cli-options
;;   ;; An option with a required argument
;;   [["-p" "--port PORT" "Port number"
;;     :default 80
;;     :parse-fn #(Integer/parseInt %)
;;     :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
;;    ;; A non-idempotent option (:default is applied first)
;;    ["-v" nil "Verbosity level"
;;     :id :verbosity
;;     :default 0
;;     :update-fn inc] ; Prior to 0.4.1, you would have to use:
;;                    ;; :assoc-fn (fn [m k _] (update-in m [k] inc))
;;    ;; A boolean option defaulting to nil
;;    ["-h" "--help"]])


(def cli-options
  [[nil "--add" "description of the task"]
   [nil "--list" "list top 10 task"]
   [nil "--update" "description of the task"]
   [nil "--delete" "description of the task"]
   [nil "--annotate" "description of the task"]
   ["-h" "--help" "Help"]
   ])

;; (defn error-msg [errors]
;;   (str "The following errors occurred while parsing your command:\n\n"
;;        (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


(def repl-options
  [:prompt #(printf "Enter the task to perform: ")
   :read   (fn [request-prompt request-exit]
             (println (read-line)))
             ;; (or ({:line-start request-prompt :stream-end request-exit}
             ;;      (main/skip-whitespace *in*))
             ;;      (re-find #"^(\d+)([\+\-\*\/])(\d+)$" (read-line))))
   :eval   (fn [args]
             (println "dummy println"))])

(defn -main [& args]
  ;;(create-task-related-tables)
  ;; (println args)
  ;; (println (parse-opts args cli-options))
  (apply main/repl repl-options)
  )

(comment
  (print "hello world")

(defn validate-args [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      ;; custom validation on arguments
      (and (= 1 (count arguments))
           (#{"start" "stop" "status"} (first arguments)))
      {:action (first arguments) :options options}
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

 )
