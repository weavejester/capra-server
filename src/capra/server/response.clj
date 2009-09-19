(ns capra.server.response
  "Functions for returning suitable HTTP responses.")

(defn- make-response
  [status data]
  {:status  status
   :headers {"Content-Type" "application/clojure"}
   :body    (pr-str data)})

(defn resource
  "Return a resource as a Clojure data structure."
  [data]
  (make-response 200 data))

(defn created
  "Return a response for a newly created resource."
  [location]
  {:status  201
   :headers {"Location" location}})

(defn bad-request
  "Return a 'bad request' response."
  [error-message]
  (make-response 400 error-message))

(defn forbidden
  "Return a 'forbidden' response."
  [error-message]
  (make-response 403 error-message))

(defn not-found
  "Return a 'not-found' response"
  [error-message]
  (make-response 404 error-message))

(defn auth-required
  "Return a 'authentication required' response"
  [realm]
  {:status 401
   :headers {"WWW-Authenticate"
             (str "Basic realm=" (pr-str realm))}})
