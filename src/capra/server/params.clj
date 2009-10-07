(ns capra.server.params
  "Middleware for parsing Clojure data structures."
  (:use clojure.contrib.duck-streams))

(defn with-clojure-params
  "Parses params passed as a serialized Clojure map in the request body."
  [handler]
  (fn [request]
    (handler
      (if (request :body)
        (let [body (slurp* (request :body))]
          (if (not= body "")
            (assoc request :params (read-string body))
            request))
        request))))
