(ns config.db.schema-migrations 
  (:require [hugsql.core :as hugsql])) 
 
(hugsql/def-db-fns "config/db/sql/schema_migrations.sql")