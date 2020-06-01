# crudik

Simple CRUD app build with Clojure/ClojureScript


## Usage

Initialise database if needed:
	$ psql -U postgres -f src/crudik/sql/init_db.sql
	$ psql -U postgres -f src/crudik/sql/init_test_db.sql

Start development server:
    $ lein ring server

## Tests

	$ lein test
