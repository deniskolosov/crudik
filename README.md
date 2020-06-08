# crudik
[![<deniskolosov>](https://circleci.com/gh/deniskolosov/crudik.svg?style=shield)](https://app.circleci.com/pipelines/github/deniskolosov/crudik)

Simple CRUD app build with Clojure/ClojureScript


## Usage

Initialise database if needed:

	$ psql -U postgres -f src/crudik/sql/init_db.sql
	$ psql -U postgres -f src/crudik/sql/init_test_db.sql

Start development server:

    $ lein ring server

## Tests

You will need to install Chrome driver for integration tests

  - `brew cask install chromedriver` for Mac users. For latest version Chrome > 83 is needed.

  Run server and

	$ lein test
