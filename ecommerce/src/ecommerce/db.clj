(ns ecommerce.db  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao []
  (d/create-database db-uri)
  (d/connect db-uri))
(defn apaga-banco []
  (d/delete-database db-uri))

;; Produtos
;; id?
;; nome String
;; slug String
;; preco ponto flutuante

;; id_entidade atributo valor
;; 15 nome Computador Novo
;; 15 slug /computador_novo
;; 15 preco 3500.10
;; 17 nome Telefone Caro
;; 17 slug /telefone
;; 17 preco 8888.88