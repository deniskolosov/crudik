(ns crudik.db
 (:require [jdbc.pool.c3p0 :as pool]
										 [hugsql.core :as hugsql]))

(def spec
  (pool/make-datasource-spec
   {:subprotocol "postgresql"
    :subname "//localhost:5432/crudik"
    :user "postgres"
    :password ""}))

(hugsql/def-db-fns "crudik/sql/patients.sql")

