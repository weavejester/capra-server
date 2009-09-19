(ns capra.server.controller
  "Capra server controller functions."
  (:require [capra.server.response :as response])
  (:require [capra.server.model.account :as account])
  (:require [capra.server.model.package :as package]))

(defn create-account
  "Create a new account."
  [new-account]
  (cond
    (nil? (new-account :name))
      (response/bad-request "Account must have a name")
    (nil? (new-account :passkey))
      (response/bad-request "Account must have a passkey")
    (account/get (new-account :name))
      (response/forbidden "Account already exists")
    :else
      (do (account/put new-account)
          (response/created (str "/" (new-account :name))))))

(defn- package-uri
  "Return the relative URI of a package."
  [pkg]
  (str "/" (pkg :account) "/" (pkg :name) "/" (pkg :version)))

(defn- account-packages
  "List all packages of an account."
  [account]
  (for [pkg (package/list account)]
    (-> pkg (select-keys [:account :name :version :description])
            (assoc :href (package-uri pkg)))))

(defn show-account
  "Show an existing account."
  [name]
  (response/resource
    (-> (account/get name)
      (dissoc :passkey)
      (assoc :packages (account-packages name)))))

(defn list-accounts
  "List all account names."
  []
  (response/resource
    (for [name (account/list-names)]
      {:name name, :href (str "/" name)})))

(defn update-account
  "Update an existing account."
  [name delta]
  (let [delta (dissoc delta :name :packages)]
    (if-let [existing (account/get name)]
      (let [updated (merge existing delta)]
        (account/put updated)
        (response/resource
          (dissoc updated :passkey))))))
