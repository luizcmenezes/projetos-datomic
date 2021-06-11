(ns ecommerce.model)

(defn novo-produto [nome slug preco]
  {:nome nome
   :slug slug
   :preco preco})