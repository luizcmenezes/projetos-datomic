(ns ecommerce.db  (:use clojure.pprint)
    (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao! []
  (d/create-database db-uri)
  (d/connect db-uri))
(defn apaga-banco! []
  (d/delete-database db-uri))

;; Produtos
;; id?
;; produto/nome String
;; produto/slug String
;; produto/preco ponto flutuante
;; categoria_id integer

;; id_entidade atributo valor
;; 15 produto/nome Computador Novo
;; 15 produto/slug /computador_novo
;; 15 produto/preco 3500.10
;; 15 :produto/categoria 37
;; 17 produto/nome Telefone Caro
;; 17 produto/slug /telefone
;; 17 produto/preco 8888.88

(def schema [;Produtos
             {:db/ident :produto/nome
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
              :db/cardinality :db.cardinality/many}
             {:db/ident :produto/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity}
             {:db/ident :produto/categoria
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}

             ;Categorias
             {:db/ident :categoria/nome
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :categoria/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity}])

(defn cria-schema! [conn]
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
  (d/q '[:find (pull ?e [*])
         :in $ ?palavra-chave
         :where [?e :produto/palavra-chave ?palavra-chave]]
       db palavra-chave))

(defn um-produto-por-dbid [db id]
  (d/pull db '[*] id))

(defn um-produto [db produto-id]
  (d/pull db '[*] [:produto/id produto-id]))

(defn todas-as-categorias [db]
  (d/q '[:find (pull ?e [*])
         :where [?e :categoria/id]] db))

(defn gera-db-adds-atribuicao-de-categoria [produtos categoria]
  (reduce (fn [db-adds produto]
            (conj db-adds  [:db/add
                            [:produto/id (:produto/id produto)]
                            :produto/categoria
                            [:categoria/id (:categoria/id categoria)]]))
          [] produtos))

(defn atribui-categorias! [conn produtos categoria]
  (let [a-transaccionar (gera-db-adds-atribuicao-de-categoria produtos categoria)]
    (d/transact conn a-transaccionar)))

(defn adiciona-produtos! [conn produtos]
  (d/transact conn produtos))

(defn adiciona-categorias! [conn categorias]
  (d/transact conn categorias))

;; query organizada com keys produto e categoria
(defn todos-os-nomes-de-produtos-e-categorias [db]
  (d/q '[:find ?nome ?nome-categoria
         :keys produto categoria
         :where [?produto :produto/nome ?nome]
         [?produto :produto/categoria ?categoria]
         [?categoria :categoria/nome ?nome-categoria]] db))

;; Exemplo com forward navigation
(defn todos-os-produtos-da-categoria [db nome-da-categoria]
  (d/q '[:find (pull ?produto [:produto/nome :produto/preco {:produto/categoria [:categoria/nome]}])
         :in $ ?nome
         :where [?categoria :categoria/nome ?nome]
         [?produto :produto/categoria ?categoria]]
       db nome-da-categoria))

;; Exemplo com backward navigation
(defn todos-os-produtos-da-categoria [db nome-da-categoria]
  (d/q '[:find (pull ?categoria [:categoria/nome {:produto/_categoria [:produto/nome :produto/preco]}])
         :in $ ?nome
         :where [?categoria :categoria/nome ?nome]]
       db nome-da-categoria))

;; Aggregates
(defn resumo-dos-produtos [db]
  (d/q '[:find (min ?preco) (max ?preco) (count ?preco)
         :with ?produto
         :keys minimo maximo quantidade
         :where [?produto :produto/preco ?preco]]
       db))

(defn resumo-dos-produtos-por-categoria [db]
  (d/q '[:find ?nome-da-categoria (min ?preco) (max ?preco) (count ?preco) (sum ?preco)
         :with ?produto
         :keys categoria minimo maximo quantidade preco-total
         :where [?produto :produto/preco ?preco]
                [?produto :produto/categoria ?categoria]
                [?categoria :categoria/nome ?nome-da-categoria]]
       db))