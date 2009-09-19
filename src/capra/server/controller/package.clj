(ns capra.server.controller.package
  "Capra server controller functions for managing packages."
  (:use capra.server.util)
  (:require [capra.server.response :as response])
  (:require [capra.server.model.package :as package]))

(defn create-package
  "Create a new package."
  [new-package]
  (cond
    (nil? (new-package :name))
      (response/bad-request "Package must have a name")
    (nil? (new-package :version))
      (response/bad-request "Package must have a version")
    (package/get (package/key new-package))
      (response/forbidden "Package already exists")
    :else
      (do (package/put (dissoc new-package :files))
          (response/created (package-uri new-package)))))

(defn show-package
  "Show an existing package."
  [account name version]
  (response/resource
    (package/get [account name version])))
