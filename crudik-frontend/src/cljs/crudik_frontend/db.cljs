(ns crudik-frontend.db)

(def default-db
  {:patients {1 {:id 1
                 :fullname "Denis Kolosov"
                 :address "Foo Bar str"
                 :sex "male"
                 :insurance "foobar123"
                 :birthdate "1900-10-10"}
              2 {:id 2
                 :fullname "Denis Kolosov"
                 :address "Foo Bar str"
                 :sex "male"
                 :insurance "foobar456"
                 :birthdate "1900-10-10"}}
   :name "re-frame"})
