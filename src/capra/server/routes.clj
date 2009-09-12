(ns capra.server.routes
  (:use capra.server.controller)
  (:use compojure.http.routes))

(defroutes public-routes
  "Routes to resources that do not need authentication."
  (GET "/accounts"
    (list-account-names))
  (POST "/accounts"
    (create-account params)))

(defroutes handler
  "Main handler function."
  public-routes
  [404 "Resource not found"])
