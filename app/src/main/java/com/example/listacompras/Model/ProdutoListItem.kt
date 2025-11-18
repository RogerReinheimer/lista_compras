package com.example.listacompras.Model

sealed class ProdutoListItem {
    data class Header(val categoriaNome: String) : ProdutoListItem()
    data class Item(val produto: Produto) : ProdutoListItem()
}