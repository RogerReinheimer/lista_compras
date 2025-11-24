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
import com.example.listacompras.Model.ProdutoListItem
import com.example.listacompras.R

class ProdutosAdapter(
    private val itensOriginais: List<ProdutoListItem>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private val itensFiltrados = mutableListOf<ProdutoListItem>()

    init {
        itensFiltrados.addAll(itensOriginais)
    }

    override fun getItemViewType(position: Int): Int {
        return when (itensFiltrados[position]) {
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

    override fun getItemCount() = itensFiltrados.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itensFiltrados[position]

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

            holder.btnEditar.setOnClickListener {
                val intent = Intent(context, ProdutoEditar::class.java)

                intent.putExtra("id", produto.id)
                intent.putExtra("categoriaId", produto.categoriaId)
                intent.putExtra("categoriaNome", produto.categoriaNome)

                intent.putExtra("nome", produto.nome)
                intent.putExtra("descricao", produto.descricao)
                intent.putExtra("preco", produto.preco)
                intent.putExtra("quantidade", produto.quantidade)
                intent.putExtra("unidade", produto.unidade)

                context.startActivity(intent)
            }

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

    // ------------------------- FUNÇÃO DE FILTRO ---------------------------- //

    fun filtrar(texto: String) {
        val termo = texto.lowercase()

        itensFiltrados.clear()

        if (termo.isEmpty()) {
            itensFiltrados.addAll(itensOriginais)
        } else {
            var categoriaAtual = ""

            itensOriginais.forEach { item ->
                if (item is ProdutoListItem.Header) {
                    categoriaAtual = item.categoriaNome
                }

                if (item is ProdutoListItem.Item) {
                    if (item.produto.nome.lowercase().contains(termo)) {

                        val existeHeader = itensFiltrados.any {
                            it is ProdutoListItem.Header && it.categoriaNome == categoriaAtual
                        }

                        if (!existeHeader) {
                            itensFiltrados.add(ProdutoListItem.Header(categoriaAtual))
                        }

                        itensFiltrados.add(item)
                    }
                }
            }
        }

        notifyDataSetChanged()
    }

    // ----------------------- VIEW HOLDERS ----------------------- //

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
