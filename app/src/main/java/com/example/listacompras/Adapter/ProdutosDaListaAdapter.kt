package com.example.listacompras.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Model.Produto
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class ProdutosDaListaAdapter(
    private val listaId: String,
    private val produtos: MutableList<Produto>,
    private val onCarrinhoUpdate: () -> Unit
) : RecyclerView.Adapter<ProdutosDaListaAdapter.ProdutoViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeProduto)
        val txtQtd: TextView = itemView.findViewById(R.id.txtQtdProduto)
        val txtPreco: TextView = itemView.findViewById(R.id.txtPrecoProduto)
        val checkProduto: CheckBox = itemView.findViewById(R.id.checkProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto_da_lista, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val p = produtos[position]

        holder.txtNome.text = p.nome
        holder.txtQtd.text = "Qtd: ${p.quantidade} ${p.unidade}"
        holder.txtPreco.text = "R$ %.2f".format(p.preco)

        holder.checkProduto.setOnCheckedChangeListener(null)
        holder.checkProduto.isChecked = p.selecionado

        holder.checkProduto.setOnCheckedChangeListener { _, marcado ->
            p.selecionado = marcado

            db.collection("listas")
                .document(listaId)
                .collection("produtos")
                .document(p.id)
                .update("selecionado", marcado)

            onCarrinhoUpdate()
        }
    }

    override fun getItemCount(): Int = produtos.size
}
