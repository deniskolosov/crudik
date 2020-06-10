# crudik
[![<deniskolosov>](https://circleci.com/gh/deniskolosov/crudik.svg?style=shield)](https://app.circleci.com/pipelines/github/deniskolosov/crudik)

Simple CRUD app build with Clojure/ClojureScript


## Usage

Initialise database if needed:

	$ psql -U postgres -f src/crudik/sql/init_db.sql
	$ psql -U postgres -f src/crudik/sql/init_test_db.sql

Start development server:

    $ lein ring server

Run in Docker:

    $ cd crudik	
    $ docker-compose up
    
## Tests

You will need to install Chrome driver for integration tests

  - `brew cask install chromedriver` for Mac users. For latest version Chrome > 83 is needed.

  Run server and

	$ lein test

## Kubernetes

    $ kubectl create -f postgres-secret.yaml     # import secrets
    $ kubectl apply -f postgres-pv.yaml          # setup persistent volume
    $ kubectl apply -f postgres-pvc.yaml         # setup persistent volume claim
    $ kubectl apply -f postgres-deployment.yaml  # setup postgres container deployment
    $ kubectl apply -f postgres-db-service.yaml  # setup postgres container service
    $ kubectl apply -f app-db-deployment.yaml    # setup app container deployment
    $ kubectl apply -f app-db-service.yaml       # setup app container service
    $ kubectl get pods                           # check pods setup is correct
    $ kubectl get services                       # check services setup is correct
    $ kubectl exec -it <pod_name> -- psql -U postgres -d crudik -c "create table if not exists patients \
        (id serial primary key, fullname varchar not null, sex varchar not null, address text not null, \
         insurance varchar not null, birthdate date not null, \
         created_at timestamp not null default current_timestamp);" # create table for patients


    $ minikube service crudik-app --url                       # get app url if using minikube

