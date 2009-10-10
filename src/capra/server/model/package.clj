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

(defn- add-file-info
  "Add file information to a package"
  [package file-info]
  (merge-with set/union package {:files #{file-info}}))

(defn put-file
  "Save a file to S3 and reference it in the SDB package."
  [package file]
  (let [bucket "capra"
        sha1 (file-sha1 file)
        url  (s3/object-url bucket sha1)
        file-info {:sha1 sha1, :href url}]
    (s3/put-file bucket sha1 file "application/java-archive")
    (s3/set-public-readonly bucket sha1)
    (put (add-file-info package file-info))
    url))

(defn list
  "List all packages for an account."
  [account]
  (query `{:select * :from "packages" :where (= :account ~account)}))
