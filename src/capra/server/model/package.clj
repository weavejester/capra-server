(ns capra.server.model.package
  "Functions for accessing the account model."
  (:refer-clojure :exclude [get key list])
  (:require [clojure.set :as set])
  (:use capra.server.util)
  (:require [capra.server.s3 :as s3])
  (:use somnium.congomongo)
  (:use clojure.contrib.def))

(defvar- bucket "capra")

(defn- get-files
  "Get a list of all files for a package."
  [package]
  (for [file (fetch :files :where {:package (package :_id)})]
    (dissoc file :_id :_ns :package)))

(defn- file-exists?
  "Does the file already exist for the package?"
  [package sha1]
  (fetch-one :files :where {:sha1 sha1, :package (package :_id)}))

(defn- file-stored?
  "Is the file already stored?"
  [sha1]
  (fetch-one :files :where {:sha1 sha1}))

(defn put-file
  "Save a file to S3 and reference it in the SDB package."
  [package file]
  (let [sha1 (file-sha1 file)
        url  (s3/object-url bucket sha1)]
    (when-not (file-stored? sha1)
      (s3/put-file bucket sha1 file "application/java-archive")
      (s3/set-public-readonly bucket sha1))
    (when-not (file-exists? package sha1)
      (insert! :files {:sha1 sha1, :href url, :package (package :_id)}))
    url))

(defn get
  "Retrieve an existing package by account, name and version."
  [[account name version]]
  (let [query {:account account, :name name, :version version}]
    (if-let [package (fetch-one :packages :where query)]
      (assoc package :files (get-files package)))))

(defn put
  "Save a package using the account, name and version as a key."
  [package]
  (insert! :packages (dissoc package :files)))

(defn list
  "List all packages for an account."
  [account]
  (fetch :packages :where {:account account}))
