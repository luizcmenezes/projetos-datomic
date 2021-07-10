(ns ecommerce.aula03
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador-novo", 2500.10M)
      celular (model/novo-produto "Celular caro", "/celular", 8888.10M)
      calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular barato", "/celular-barato", 0.1M)]
  (d/transact conn [computador
                    celular
                    celular-barato
                    calculadora]))

(db/todos-os-produtos (d/db conn))
(db/todos-os-produtos-por-slug (d/db conn) "/computador-novo")
(db/todos-os-slugd (d/db conn))
(db/todos-os-produtos-por-nome (d/db conn))
(clojure.pprint/pprint (db/todos-os-produtos-por-preco (d/db conn)))


(db/apaga-banco)