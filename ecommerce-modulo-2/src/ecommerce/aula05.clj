(ns ecommerce.aula05
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao!))

(pprint (db/cria-schema! conn))

(def eletronicos (model/nova-categroia "Eletrônicos"))
(def esporte (model/nova-categroia "Esporte"))

(pprint @(db/adiciona-categorias! conn [eletronicos esporte]))


(def categorias (db/todas-as-categorias (d/db conn)))
(pprint categorias)

(def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.10M))
(def celular (model/novo-produto (model/uuid) "Celular caro", "/celular", 8888.10M))
(def calculadora {:produto/nome "Calculadora com 4 operações"})
(def celular-barato (model/novo-produto "Celular barato", "/celular-barato", 0.1M))
(def xadez (model/novo-produto "Tabuleiro de xadrez" "/tabuleiro-de-xadrez" 30M))

(pprint @(db/adiciona-produtos! conn [computador celular celular-barato calculadora xadez]))

(pprint (db/atribui-categorias! conn [computador celular celular-barato] eletronicos))
(pprint (db/atribui-categorias! conn [xadez] esporte))

(pprint @(db/adiciona-produtos! conn [{:produto/nome "Camiseta"
                                       :produto/slug "/camiseta"
                                       :produto/preco 30M
                                       :produto/id (model/uuid)
                                       :produto/categoria {:categoria/nome "Roupa"
                                                           :categoria/id (model/uuid)}}]))

(def esporte-id (:categoria/id esporte))
(pprint @(db/adiciona-produtos! conn [{:produto/nome "Dama"
                                       :produto/slug "/dama"
                                       :produto/preco 15M
                                       :produto/id (model/uuid)
                                       :produto/categoria [:categoria/id esporte-id]}]))

(pprint (db/todos-os-produtos (d/db conn)))
(pprint (db/todos-os-nomes-de-produtos-e-categorias (d/db conn)))
(pprint (db/resumo-dos-produtos (d/db conn)))
(pprint (db/resumo-dos-produtos-por-categoria (d/db conn)))


;; (db/apaga-banco!)
