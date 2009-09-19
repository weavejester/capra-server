(ns capra.server.routes
  (:use capra.server.controller)
  (:use compojure.http.routes))

(defroutes public-routes
  "Routes to resources that do not need authentication."
  (GET "/"
    (list-accounts))
  (POST "/"
    (create-account params))
  (GET "/:account"
    (show-account (params :account))))

(defroutes handler
  "Main handler function."
  public-routes
  [404 "Resource not found"])
