-- :name create-schema-migrations-table :! 
create table if not exists schema_migrations (
  version integer not null,
  created_at timestamp not null default current_timestamp
)
 
-- :name current-db-version :? :1 
select * from schema_migrations
order by created_at
 
-- :name insert-db-version :! :n 
insert into schema_migrations (version)
  values (:version)