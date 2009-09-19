(ns capra.server.util
  "Common utility functions for the Capra server.")

(defn account-uri
  [account]
  (str "/" (account :name)))

(defn package-uri
  "Return the relative URI of a package."
  [pkg]
  (str "/" (pkg :account) "/" (pkg :name) "/" (pkg :version)))
