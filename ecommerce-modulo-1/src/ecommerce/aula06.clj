(ns ecommerce.aula06
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
  (pprint @(d/transact conn [computador
                             celular
                             celular-barato
                             calculadora])))

(pprint (db/todos-os-produtos-por-preco-minimo (d/db conn) 3000.0M))

(pprint (count (db/todos-os-produtos-por-preco-minimo (d/db conn) 2000.0M)))

(pprint (count (db/todos-os-produtos-por-preco-minimo (d/db conn) 5000.0M)))


(d/transact conn [[:db/add 17592186045418 :produto/palavra-chave "desktop"]
                  [:db/add 17592186045418 :produto/palavra-chave "computador"]])

(pprint (db/todos-os-produtos (d/db conn)))

(d/transact conn [[:db/retract 17592186045418 :produto/palavra-chave "computador"]])

(pprint (db/todos-os-produtos (d/db conn)))


(d/transact conn [[:db/add 17592186045418 :produto/palavra-chave "Monitor preto e branco"]])

(pprint (db/todos-os-produtos (d/db conn)))


(d/transact conn [[:db/add 17592186045419 :produto/palavra-chave "smartphone"]
                  [:db/add 17592186045420 :produto/palavra-chave "smartphone"]])

(pprint (db/todos-os-produtos (d/db conn)))

(pprint (db/todos-os-produtos-por-palavra-chave (d/db conn) "smartphone"))

(pprint (db/todos-os-produtos-por-palavra-chave (d/db conn) "desktop"))

(pprint (db/consulta-produto-por-id (d/db conn) 17592186045418))

;; (db/apaga-banco)