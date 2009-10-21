(ns capra.server.routes
  (:use capra.server.controller.account)
  (:use capra.server.controller.package)
  (:use capra.server.auth)
  (:use capra.server.params)
  (:use compojure.control)
  (:use compojure.http.routes))

(defn with-debug [handler]
  (fn [request]
    (prn request)
    (handler request)))

(defroutes public-routes
  "Routes to resources that do not need authentication."
  (GET "/"
    (list-accounts))
  (POST "/"
    (create-account params))
  (GET "/:account"
    (show-account (params :account)))
  (GET "/:account/:package/*"
    (show-package (params :account)
                  (params :package)
                  (params :*))))

(defroutes private-routes
  (PUT "/:account"
    (update-account (params :account)
                    (dissoc params :account)))
  (POST "/:account"
    (create-package params))
  (PUT "/:account/:package/*"
    (update-package (params :account)
                    (params :package)
                    (params :*)
                    (dissoc params :account :package :*)))
  (POST "/:account/:package/*"
    (upload-package-file (params :account)
                         (params :package)
                         (params :*)
                         (request :body))))

(decorate private-routes
  with-account-auth)

(defroutes handler
  "Main handler function."
  public-routes
  private-routes
  (ANY "*" [404 "Resource not found"]))

(decorate handler
  with-clojure-params
  with-debug)
