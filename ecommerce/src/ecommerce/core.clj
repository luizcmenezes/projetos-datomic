(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao))
(pprint conn)

(pprint
 (model/novo-produto "Computador Novo", "/computador_novo", 2500.10))