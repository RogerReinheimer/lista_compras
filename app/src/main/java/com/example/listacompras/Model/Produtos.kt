package com.example.listacompras.Model

data class Produto(
    var id: String = "",
    var nome: String = "",
    var descricao: String = "",
    var quantidade: Double = 0.0,
    var unidade: String = "",
    var preco: Double = 0.0,
    var categoriaId: String = "",
    var categoriaNome: String = ""
)