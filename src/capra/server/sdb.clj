(ns capra.server.sdb
  "Functions for getting SDB data."
  (:require [org.clojure.sdb :as sdb])
  (:use clojure.contrib.def)
  (:use clojure.contrib.java-utils))

(defvar- aws-client
  (sdb/create-client
    user/aws-key
    user/aws-secret-key))

(defn get-attrs
  "Return a map of a stored value without the sdb/id key."
  [bucket id]
  (let [value (sdb/get-attrs aws-client (as-str bucket) id)]
    (dissoc value :sdb/id)))

(defn put-attrs
  "Store a map of attributes in SimpleDB"
  [bucket attrs]
  (sdb/put-attrs aws-client (as-str bucket) attrs))

(defn query
  "Query SimpleDB."
  [clause]
  (sdb/query aws-client clause))
