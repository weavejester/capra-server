(ns capra.server.params
  "Middleware for parsing Clojure data structures."
  (:use clojure.contrib.duck-streams))

(defn- clojure-type?
  "Is the request using the application/clojure content type?"
  [request]
  (= (get-in request [:headers "content-type"])
     "application/clojure"))

(defn with-clojure-params
  "Parses params passed as a serialized Clojure map in the request body."
  [handler]
  (fn [request]
    (handler
      (if (and (clojure-type? request) (request :body))
        (let [body (slurp* (request :body))]
          (if (not= body "")
            (assoc request :params (read-string body))
            request))
        request))))
