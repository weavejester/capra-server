(ns capra.server.controller
  "Capra server controller functions."
  (:require [capra.server.response :as response])
  (:require [capra.server.model.account :as account]))

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

(defn show-account
  "Show an existing account."
  [name]
  (response/resource
    (dissoc (account/get name) :passkey)))

(defn list-accounts
  "List all account names."
  []
  (response/resource
    (for [name (account/list-names)]
      {:name name, :href (str "/" name)})))

(defn update-account
  "Update an existing account."
  [name delta]
  (let [delta (dissoc delta :name)]
    (if-let [existing (account/get name)]
      (let [updated (merge existing delta)]
        (account/put updated)
        (response/resource
          (dissoc updated :passkey))))))
