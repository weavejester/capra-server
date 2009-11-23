(ns capra.server.model.account
  "Functions for accessing the account model."
  (:refer-clojure :exclude [get])
  (:use capra.server.util)
  (:use somnium.congomongo)
  (:import BCrypt))

(defn get
  "Retrieve an existing account by name."
  [name]
  (fetch-one :accounts :where {:name name}))

(defn- hash-passkey
  "Ensure the passkey is hashed before saving."
  [account]
  (update-in account [:passkey]
    (fn [passkey]
      (BCrypt/hashpw passkey (BCrypt/gensalt)))))

(defn put
  "Save a new account using the name as a key."
  [account]
  (insert! :accounts (hash-passkey account)))

(defn list-names
  "Retrieve all current account names."
  []
  (map :name (fetch :accounts)))

(defn valid?
  "Is the supplied account name and passkey valid?"
  [name pass]
  (if (and name pass)
    (BCrypt/checkpw pass (:passkey (get name)))))
