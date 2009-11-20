(ns capra.server.controller.package
  "Capra server controller functions for managing packages."
  (:use capra.server.util)
  (:require [capra.server.response :as response])
  (:require [capra.server.model.package :as package])
  (:use clojure.contrib.duck-streams)
  (:use clojure.contrib.http.agent)
  (:import java.io.File))

(defn create-package
  "Create a new package."
  [new-package]
  (cond
    (nil? (new-package :account))
      (response/bad-request "Package must have an account")
    (nil? (new-package :name))
      (response/bad-request "Package must have a name")
    (nil? (new-package :version))
      (response/bad-request "Package must have a version")
    (package/get [(new-package :account)
                  (new-package :name)
                  (new-package :version)])
      (response/forbidden "Package already exists")
    :else
      (do (package/put (dissoc new-package :files :_id :_ns))
          (response/created (package-uri new-package)))))

(defn show-package
  "Show an existing package."
  [account name version]
  (response/resource
    (dissoc (package/get [account name version])
      :_id :_ns)))

(defn update-package
  "Update an existing package."
  [account name version delta]
  (let [delta (dissoc delta :account :name :files :_id :_ns)]
    (if-let [existing (package/get [account name version])]
      (let [updated (merge existing delta)]
        (package/put updated)
        (response/resource updated)))))

(defn upload-package-file
  "Upload a file into a package."
  [account name version stream]
  (if-let [pkg (package/get [account name version])]
    (let [temp-file (File/createTempFile "capra" ".jar")]
      (copy stream temp-file)
      (response/created (package/put-file pkg temp-file)))))
