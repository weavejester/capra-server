(ns capra.server.model.package
  "Functions for accessing the account model."
  (:refer-clojure :exclude [get key list])
  (:use capra.server.sdb))

(defn get
  "Retrieve an existing package by account, name and version."
  [account name version]
  (let [package (get-attrs :packages [account name version])]
    (if (package :name)
      package)))

(defn key
  "Return a unique key for a package"
  [package]
  [(package :account) (package :name) (package :version)])

(defn put
  "Save a new package using the account, name and version as a key."
  [package]
  (put-attrs :package
    (assoc package :sdb/id (key package))))

(defn list
  "List all packages for an account."
  [account]
  (query `{:select * :from "packages" :where (= :account ~account)}))
