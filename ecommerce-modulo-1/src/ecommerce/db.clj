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
;; produto/nome String
;; produto/slug String
;; produto/preco ponto flutuante

;; id_entidade atributo valor
;; 15 produto/nome Computador Novo
;; 15 produto/slug /computador_novo
;; 15 produto/preco 3500.10
;; 17 produto/nome Telefone Caro
;; 17 produto/slug /telefone
;; 17 produto/preco 8888.88

(def schema [{:db/ident :produto/nome
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "O nome de um produto"}
             {:db/ident :produto/slug
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "O caminho para acessar esse produto via http"}
             {:db/ident :produto/preco
              :db/valueType :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc "O preço de um produto com precisão monetária"}
             {:db/ident :produto/palavra-chave
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/many}])

(defn cria-schema [conn]
  (d/transact conn schema))

;; (defn todos-os-produtos [db]
;;   (d/q '[:find ?entidade
;;          :where [?entidade :produto/nome]] db))

(defn todos-os-produtos-por-slug [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug
         :where [?entidade :produto/slug ?slug]]
       db slug))

(defn todos-os-slugd [db]
  (d/q '[:find ?qualquer-valor
         :where [?entidade :produto/slug ?qualquer-valor]] db))

(defn todos-os-produtos-por-nome [db]
  (d/q '[:find ?qualquer-produto
         :where [?entidade :produto/nome ?qualquer-produto]] db))

(defn todos-os-produtos-por-preco [db]
  (d/q '[:find ?nome
         ?preco
         :keys nome
         preco
         :where [?e :produto/preco ?preco]
         [?e :produto/nome ?nome]]
       db))

;; Outra forma
(defn todos-os-produtos-por-preco [db]
  (d/q '[:find ?nome
         ?preco
         :keys produto/nome
         produto/preco
         :where [?e :produto/preco ?preco]
         [?e :produto/nome ?nome]]
       db))

;; outra forma explicita uso do pull
;; (defn todos-os-produtos [db]
;;   (d/q '[:find (pull ?entidade [:produto/nome 
;;                                 :produto/preco 
;;                                 :produto/slug])
;;          :where [?entidade :produto/nome]] db))

;; outra forma generica uso do pull
(defn todos-os-produtos [db]
  (d/q '[:find (pull ?entidade [*])
         :where [?entidade :produto/nome]] db))

(defn consulta-produto-por-id [db id]
  (d/q '[:find (pull ?id [*])
         :in $ ?id
         :where [?id :produto/nome]]
       db id))

;; Consulta normal
(defn todos-os-produtos-por-preco-minimo [db preco-minimo]
  (d/q '[:find ?nome ?preco
         :in $ ?preco-minimo
         :keys produto/nome produto/preco
         :where [?e :produto/preco ?preco]
         [?e :produto/nome ?nome]
         [(>  ?preco ?preco-minimo)]]
       db preco-minimo))

;; Consulta com 
(defn todos-os-produtos-por-preco-minimo [db preco-minimo-req]
  (d/q '[:find ?nome ?preco
         :in $ ?preco-minimo
         :keys produto/nome produto/preco
         :where [?e :produto/preco ?preco]
         [(>  ?preco ?preco-minimo)]
         [?e :produto/nome ?nome]]
       db preco-minimo-req))


(defn todos-os-produtos-por-palavra-chave [db palavra-chave]
  (d/q '[:find (pull ?e [*] )
         :in $ ?palavra-chave
         :where [?e :produto/palavra-chave ?palavra-chave]]
   db palavra-chave))