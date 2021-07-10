(ns ecommerce.aula05
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador-novo", 2500.10M)
      celular (model/novo-produto "Celular caro", "/celular", 8888.10M)
      resultado @(d/transact conn [computador celular])]
  (pprint resultado))

(let [calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular barato", "/celular-barato", 0.1M)
      resultado @(d/transact conn [celular-barato calculadora])]
  (pprint resultado))

;; Consulta um snapshot no instante do d/db
(clojure.pprint/pprint(count (db/todos-os-produtos (d/db conn))))

;; rodando a query num banco filtrado com dados do passado
(pprint (count (db/todos-os-produtos (d/as-of (d/db conn) #inst "2021-06-24T21:22:32.067-00:00"))))

;; (db/apaga-banco)