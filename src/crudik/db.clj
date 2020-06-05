(ns crudik.db
 (:require [jdbc.pool.c3p0 :as pool]
										 [hugsql.core :as hugsql]))

(def spec
  (pool/make-datasource-spec
   {:subprotocol "postgresql"
    :subname (format "//%s:5432/crudik" (get (System/getenv) "POSTGRES_HOST" "localhost")) ;get postgres host from environment var
    :user "postgres"
    :password ""}))

(hugsql/def-db-fns "crudik/sql/patients.sql")

