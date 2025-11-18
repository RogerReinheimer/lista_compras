package com.example.listacompras.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Activities.ProdutoEditar
import com.example.listacompras.R
import com.example.listacompras.Model.ProdutoListItem

class ProdutosAdapter(
    private val itens: List<ProdutoListItem>,
    private val context: Context,
    private val categoriaId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return when (itens[position]) {
            is ProdutoListItem.Header -> TYPE_HEADER
            is ProdutoListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header_categoria, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_produtos_item, parent, false)
            ProdutoViewHolder(view)
        }
    }

    override fun getItemCount() = itens.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itens[position]

        if (holder is HeaderViewHolder && item is ProdutoListItem.Header) {
            holder.txtHeader.text = item.categoriaNome
        }

        if (holder is ProdutoViewHolder && item is ProdutoListItem.Item) {

            val produto = item.produto

            holder.txtNome.text = produto.nome
            holder.txtDescricao.text = "Descrição: ${produto.descricao}"
            holder.txtPreco.text = "Preço: R$ ${produto.preco}"
            holder.txtQuantidade.text = "Quantidade: ${produto.quantidade}"
            holder.txtUnidade.text = "Unidade: ${produto.unidade}"

            // -------- EDITAR --------
            holder.btnEditar.setOnClickListener {
                val intent = Intent(context, ProdutoEditar::class.java)

                intent.putExtra("id", produto.id) // ⚡ ID correto do Firestore
                intent.putExtra("categoriaId", produto.categoriaId) // ⚡ categoria real
                intent.putExtra("categoriaNome", produto.categoriaNome)

                intent.putExtra("nome", produto.nome)
                intent.putExtra("descricao", produto.descricao)
                intent.putExtra("preco", produto.preco)
                intent.putExtra("quantidade", produto.quantidade)
                intent.putExtra("unidade", produto.unidade)

                context.startActivity(intent)
            }

            // -------- EXPANDIR / RETRAIR --------
            holder.header.setOnClickListener {
                if (holder.container.visibility == View.GONE) {
                    holder.container.visibility = View.VISIBLE
                    holder.btnExpandir.setImageResource(R.drawable.flecha_cima)
                } else {
                    holder.container.visibility = View.GONE
                    holder.btnExpandir.setImageResource(R.drawable.flecha_baixo)
                }
            }
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHeader: TextView = itemView.findViewById(R.id.txtHeaderCategoria)
    }

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val header: LinearLayout = itemView.findViewById(R.id.headerProduto)
        val container: LinearLayout = itemView.findViewById(R.id.containerDetalhesProduto)

        val txtNome: TextView = itemView.findViewById(R.id.txtNomeProdutoCard)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtDescricaoProdutoCard)
        val txtPreco: TextView = itemView.findViewById(R.id.txtPrecoProdutoCard)
        val txtQuantidade: TextView = itemView.findViewById(R.id.txtQuantidadeProdutoCard)
        val txtUnidade: TextView = itemView.findViewById(R.id.txtUnidadeProdutoCard)

        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarProdutoCard)
        val btnExpandir: ImageView = itemView.findViewById(R.id.btnExpandirProduto)
    }
}
