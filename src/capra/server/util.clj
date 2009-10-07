(ns capra.server.util
  "Common utility functions for the Capra server."
  (:import java.io.FileInputStream)
  (:import org.apache.commons.codec.digest.DigestUtils))

(defn account-uri
  [account]
  (str "/" (account :name)))

(defn package-uri
  "Return the relative URI of a package."
  [pkg]
  (str "/" (pkg :account) "/" (pkg :name) "/" (pkg :version)))

(defn file-sha1
  "Find the SHA1 hex digest of a specified file."
  [file]
  (DigestUtils/shaHex (FileInputStream. file)))
