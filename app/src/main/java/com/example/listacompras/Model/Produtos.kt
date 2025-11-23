package com.example.listacompras.Model

import android.os.Parcel
import android.os.Parcelable

data class Produto(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val quantidade: Double = 0.0,
    val unidade: String = "",
    val preco: Double = 0.0,
    val categoriaId: String = "",
    val categoriaNome: String = "",
    var selecionado: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nome)
        parcel.writeString(descricao)
        parcel.writeDouble(quantidade)
        parcel.writeString(unidade)
        parcel.writeDouble(preco)
        parcel.writeString(categoriaId)
        parcel.writeString(categoriaNome)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Produto> {
        override fun createFromParcel(parcel: Parcel): Produto = Produto(parcel)
        override fun newArray(size: Int): Array<Produto?> = arrayOfNulls(size)
    }
}
