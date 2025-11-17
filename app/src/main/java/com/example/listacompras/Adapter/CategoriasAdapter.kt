package com.example.listacompras.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Model.Categoria
import com.example.listacompras.R

class CategoriasAdapter(
    private val lista: List<Categoria>,
    private val onClickEditar: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriasAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeCategoriaCard)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarCategoriaCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = lista[position]

        holder.txtNome.text = categoria.nome

        holder.btnEditar.setOnClickListener {
            onClickEditar(categoria)
        }
    }

    override fun getItemCount() = lista.size
}
