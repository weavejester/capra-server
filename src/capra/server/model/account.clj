(ns capra.server.model.account
  "Functions for accessing the account model."
  (:refer-clojure :exclude [get])
  (:use capra.server.util)
  (:use somnium.congomongo))

(defn get
  "Retrieve an existing account by name."
  [name]
  (fetch-one :accounts :where {:name name}))

(defn put
  "Save a new account using the name as a key."
  [account]
  (insert! :accounts account))

(defn list-names
  "Retrieve all current account names."
  []
  (map :name (fetch :accounts)))

(defn valid?
  "Is the supplied account name and passkey valid?"
  [name pass]
  (if (and name pass)
    (= (:passkey (get name)) pass)))
