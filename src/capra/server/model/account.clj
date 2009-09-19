(ns capra.server.model.account
  "Functions for accessing the account model."
  (:refer-clojure :exclude [get])
  (:use capra.server.sdb))

(defn get
  "Retrieve an existing account by name."
  [name]
  (let [account (get-attrs :accounts name)]
    (if (account :name)
      account)))

(defn put
  "Save a new account using the name as a key."
  [account]
  (put-attrs :accounts
    (assoc account :sdb/id (account :name))))

(defn list-names
  "Retrieve all current account names."
  []
  (let [accounts (query '{:select [:name] :from "accounts"})]
    (map :name accounts)))

(defn valid?
  "Is the supplied account name and passkey valid?"
  [name pass]
  (if (and name pass)
    (= (:passkey (get name)) pass)))
