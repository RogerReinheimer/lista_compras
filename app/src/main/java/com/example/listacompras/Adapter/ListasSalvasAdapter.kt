package com.example.listacompras.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Model.Lista
import com.example.listacompras.Model.Produto
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class ListasSalvasAdapter(
    private val listas: ArrayList<Lista>,
    private val onClick: (Lista) -> Unit
) : RecyclerView.Adapter<ListasSalvasAdapter.ListaViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val estados = hashMapOf<String, Boolean>()

    inner class ListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeListaCard)
        val txtData: TextView = itemView.findViewById(R.id.txtDataListaCard)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotalListaCard)
        val txtTotalCarrinho: TextView = itemView.findViewById(R.id.txtTotalCarrinhoListaCard)

        val btnExpandir: ImageView = itemView.findViewById(R.id.btnExpandirLista)
        val containerProdutos: LinearLayout = itemView.findViewById(R.id.containerProdutosLista)
        val recyclerProdutos: RecyclerView = itemView.findViewById(R.id.recyclerProdutosDaLista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lista, parent, false)
        return ListaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        val lista = listas[position]

        holder.txtNome.text = lista.nome
        holder.txtTotal.text = "Total: R$ %.2f".format(lista.total)

        lista.dataCriacao?.let {
            holder.txtData.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
        }

        val expandida = estados[lista.id] ?: false
        holder.containerProdutos.visibility = if (expandida) View.VISIBLE else View.GONE
        holder.btnExpandir.setImageResource(
            if (expandida) R.drawable.flecha_cima else R.drawable.flecha_baixo
        )

        holder.recyclerProdutos.layoutManager =
            LinearLayoutManager(holder.itemView.context)

        holder.itemView.setOnClickListener { onClick(lista) }

        holder.btnExpandir.setOnClickListener {
            val novoEstado = !(estados[lista.id] ?: false)
            estados[lista.id] = novoEstado
            notifyItemChanged(position)

            if (novoEstado) carregarProdutos(lista.id, holder)
        }

        if (expandida) carregarProdutos(lista.id, holder)
    }

    override fun getItemCount() = listas.size

    private fun carregarProdutos(listaId: String, holder: ListaViewHolder) {
        db.collection("listas")
            .document(listaId)
            .collection("produtos")
            .get()
            .addOnSuccessListener { snap ->

                val produtos = snap.map { doc ->
                    Produto(
                        id = doc.id,
                        nome = doc.getString("nome") ?: "",
                        descricao = "",
                        quantidade = doc.getDouble("quantidade") ?: 0.0,
                        unidade = doc.getString("unidade") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoriaId = "",
                        categoriaNome = "",
                        selecionado = doc.getBoolean("selecionado") ?: false
                    )
                }.toMutableList()

                holder.recyclerProdutos.adapter = ProdutosDaListaAdapter(
                    listaId = listaId,
                    produtos = produtos,
                    onCarrinhoUpdate = {
                        val totalCarrinho = produtos.filter { it.selecionado }
                            .sumOf { it.preco * it.quantidade }

                        holder.txtTotalCarrinho.text =
                            "Carrinho: R$ %.2f".format(totalCarrinho)
                    }
                )

                val totalCarrinho = produtos.filter { it.selecionado }
                    .sumOf { it.preco * it.quantidade }

                holder.txtTotalCarrinho.text =
                    "Carrinho: R$ %.2f".format(totalCarrinho)
            }
    }
}
