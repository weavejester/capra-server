(ns capra.server.auth
  (:require [capra.server.model.account :as account])
  (:require [capra.server.response :as response])
  (:use compojure.encodings)
  (:use compojure.http.request)
  (:use clojure.contrib.def))

(defvar- re-basic-auth #"Basic\s+([A-Za-z0-9+/=]+)"
  "Regex for matching basic HTTP authorization headers")

(defn- get-auth-info
  "Return the username and password in the HTTP request."
  [request]
  (if-let [header (get-in request [:headers "authorization"])]
    (if-let [[_ auth] (re-matches re-basic-auth header)]
      (.split (base64-decode auth) ":" 2))))

(defn- get-account
  [request]
  (second (re-matches #"/([\w-]+).*" (request :uri))))

(defn with-account-auth
  "Only show resource to user with access to the account."
  [handler]
  (fn [request]
    (let [[user pass] (get-auth-info request)
          req-account (get-account request)]
      (prn pass)
      (if (and (= user req-account)
               (account/valid? user pass))
        (handler request)
        (response/auth-required req-account)))))
