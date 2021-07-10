(ns ecommerce.aula01
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao!))

(db/cria-schema! conn)

(let [computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.10M)
      celular (model/novo-produto (model/uuid) "Celular caro", "/celular", 8888.10M)
      calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular barato", "/celular-barato", 0.1M)]
  (pprint @(d/transact conn [computador
                             celular
                             celular-barato
                             calculadora])))

(def produtos (db/todos-os-produtos (d/db conn)))
(def primeiro-dbid (-> produtos
                             ffirst
                             :db/id))
(pprint (db/um-produto-por-dbid (d/db conn) primeiro-dbid))

(pprint produtos)

(def primeiro-produto-id (-> produtos
                               ffirst
                               :produto/id))

(pprint (db/um-produto (d/db conn) primeiro-produto-id))

;; (db/apaga-banco!)