(ns capra.server.model.package
  "Functions for accessing the account model."
  (:refer-clojure :exclude [get key list])
  (:require [clojure.set :as set])
  (:use capra.server.sdb)
  (:use capra.server.util)
  (:require [capra.server.s3 :as s3]))

(defn get
  "Retrieve an existing package by account, name and version."
  [[account name version]]
  (let [package (get-attrs :packages (str account "/" name "/" version))]
    (if (package :name)
      (assoc package :files (read-string (package :files "#{}"))))))

(defn key
  "Return a unique key for a package"
  [package]
  (str (package :account) "/" (package :name) "/" (package :version)))

(defn put
  "Save a package using the account, name and version as a key."
  [package]
  (put-attrs :packages
    (assoc package :sdb/id (key package)
                   :type   :package
                   :files  (pr-str (package :files)))))

(defn put-file
  "Save a file to S3 and reference it in the SDB package."
  [package file]
  (let [bucket  "capra"
        object  (file-sha1 file)
        url     (s3/object-url bucket object)
        package (merge-with set/union package {:files #{url}})]
    (s3/put-file bucket object file "application/java-archive")
    (s3/set-public-readonly bucket object)
    (put package)
    url))

(defn list
  "List all packages for an account."
  [account]
  (query `{:select * :from "packages" :where (= :account ~account)}))
