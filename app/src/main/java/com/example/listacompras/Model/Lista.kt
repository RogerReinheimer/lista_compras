package com.example.listacompras.Model

import java.util.Date

data class Lista(
    var id: String = "",
    var nome: String = "",
    var total: Double = 0.0,
    var dataCriacao: Date? = null
)
