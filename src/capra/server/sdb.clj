(ns capra.server.sdb
  "Functions for interacting with Amazon SimpleDB."
  (:require [org.clojure.sdb :as sdb])
  (:use clojure.contrib.def)
  (:use clojure.contrib.java-utils))

(defvar- aws-client
  (sdb/create-client
    user/aws-key
    user/aws-secret-key))

(defn create-domain
  "Create a new domain."
  [domain]
  (sdb/create-domain aws-client (as-str domain)))

(defn get-attrs
  "Return a map of a stored value without the sdb/id key."
  [domain id]
  (let [value (sdb/get-attrs aws-client (as-str domain) id)]
    (dissoc value :sdb/id)))

(defn put-attrs
  "Store a map of attributes in SimpleDB."
  [domain attrs]
  (sdb/put-attrs aws-client (as-str domain) attrs))

(defn delete-attrs
  "Delete attributes from SimpleDB."
  [domain attrs]
  (sdb/delete-attrs aws-client (as-str domain) attrs))

(defn query
  "Query SimpleDB."
  [clause]
  (sdb/query aws-client clause))
