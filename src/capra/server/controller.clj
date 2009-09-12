(ns capra.server.controller
  "Capra server controller functions."
  (:use capra.server.sdb))

(defn- get-account
  "Retrieve an existing account by name."
  [name]
  (get-attrs :accounts name))

(defn- put-account
  "Save a new account using the name as a key."
  [account]
  (put-attrs :accounts
    (assoc account :sdb/id (account :name))))

(defn- get-account-names
  "Retrieve all current account names."
  []
  (query '{:select [:name] :from "accounts"}))

(defn create-account
  "Create a new account."
  [account]
  (cond
    (nil? (account :name))
      [400 "Account must have a name"]
    (nil? (account :passkey))
      [400 "Account must have a passkey"]
    (get-account (account :name))
      [403 "Account already exists"]
    :if-valid
      (do (put-account account)
          [201 "Account created"])))

(defn list-account-names
  "List all account names."
  []
  (prn-str (get-account-names)))
