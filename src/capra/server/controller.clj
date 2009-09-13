(ns capra.server.controller
  "Capra server controller functions."
  (:require [capra.server.model.account :as account]))

(defn create-account
  "Create a new account."
  [new-account]
  (cond
    (nil? (new-account :name))
      [400 "Account must have a name"]
    (nil? (new-account :passkey))
      [400 "Account must have a passkey"]
    (account/get (new-account :name))
      [403 "Account already exists"]
    :if-valid
      (do (account/put new-account)
          [201 "Account created"])))

(defn list-account-names
  "List all account names."
  []
  (prn-str (account/list-names)))
