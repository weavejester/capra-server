(ns capra.server.routes
  (:use capra.server.controller.account)
  (:use capra.server.controller.package)
  (:use capra.server.auth)
  (:use compojure.control)
  (:use compojure.http.routes))

(defroutes public-routes
  "Routes to resources that do not need authentication."
  (GET "/"
    (list-accounts))
  (POST "/"
    (create-account params))
  (GET "/:account"
    (show-account (params :account))))

(defroutes private-routes
  (PUT "/:account"
    (update-account (params :account)
                    (dissoc params :account)))
  (POST "/:account"
    (create-package params)))

(decorate private-routes
  with-account-auth)

(defroutes handler
  "Main handler function."
  public-routes
  private-routes
  (ANY "*" [404 "Resource not found"]))
