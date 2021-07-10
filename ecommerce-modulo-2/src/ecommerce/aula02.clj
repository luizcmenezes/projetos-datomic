(ns ecommerce.aula02
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao!))

(db/cria-schema! conn)

(def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.10M))
(def celular (model/novo-produto (model/uuid) "Celular caro", "/celular", 8888.10M))
(def calculadora {:produto/nome "Calculadora com 4 operações"})
(def celular-barato (model/novo-produto "Celular barato", "/celular-barato", 0.1M))

(def celular-barato-2 (model/novo-produto (:produto/id celular-barato) "CELULAR DA CHINA" "/celuar-baratissimo" 0.001M))

(pprint @(d/transact conn [computador
                           celular
                           celular-barato
                           calculadora]))

(pprint @(d/transact conn [celular-barato-2]))

(def produtos (db/todos-os-produtos (d/db conn)))
(pprint produtos)

;; (db/apaga-banco!)