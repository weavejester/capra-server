(ns capra.server.s3
  "Functions for interacting with Amazon S3."
  (:use clojure.contrib.def)
  (:import org.jets3t.service.security.AWSCredentials)
  (:import org.jets3t.service.impl.rest.httpclient.RestS3Service)
  (:import org.jets3t.service.model.S3Object)
  (:import org.jets3t.service.acl.AccessControlList)
  (:import org.jets3t.service.acl.GroupGrantee)
  (:import org.jets3t.service.acl.Permission))

(defvar- service
  (RestS3Service. (AWSCredentials. user/aws-key user/aws-secret-key))
  "S3 service object")

(defn object-url
  "Return the URL of an amazon S3 object."
  [bucket object]
  (str "http://" bucket ".s3.amazonaws.com/" object))

(defn- s3-object
  "Create an S3 object from a file."
  [object file type]
  (doto (S3Object. object)
    (.setDataInputFile file)
    (.setContentType type)))

(defn put-file
  "Add a file to a bucket."
  [bucket object file type]
  (.putObject service bucket (s3-object object file type)))

(defn- public-readonly-acl
  "Return a ACL object for readonly public access."
  [bucket]
  (doto (AccessControlList.)
    (.setOwner (.. service
                 (getBucket bucket)
                 (getOwner)))
    (.grantPermission GroupGrantee/ALL_USERS
                      Permission/PERMISSION_READ)))

(defn set-public-readonly
  "Make a file readonly to all users."
  [bucket object]
  (.putObjectAcl service bucket object (public-readonly-acl bucket)))
