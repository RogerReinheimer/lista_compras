package com.example.listacompras.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Model.Produto
import com.example.listacompras.Model.ProdutoListItem
import com.example.listacompras.R

class SelecionarProdutosAdapter(
    private val itensOriginais: List<ProdutoListItem>,
    private val selecionados: MutableSet<String>,
    private val onClick: (Produto, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itensFiltrados = itensOriginais.toMutableList()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
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
            HeaderVH(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_produto_selecao, parent, false)
            ItemVH(view)
        }
    }

    override fun getItemCount() = itensFiltrados.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = itensFiltrados[position]) {
            is ProdutoListItem.Header -> (holder as HeaderVH).txt.text = item.categoriaNome
            is ProdutoListItem.Item -> (holder as ItemVH).bind(item.produto)
        }
    }

    fun filtrar(texto: String) {
        val busca = texto.lowercase().trim()

        if (busca.isEmpty()) {
            itensFiltrados = itensOriginais.toMutableList()
        } else {
            val novaLista = mutableListOf<ProdutoListItem>()
            var categoriaAtual: String? = null

            itensOriginais.forEach { item ->
                if (item is ProdutoListItem.Header) {
                    categoriaAtual = item.categoriaNome
                } else if (item is ProdutoListItem.Item) {
                    if (item.produto.nome.lowercase().contains(busca)) {

                        val header = ProdutoListItem.Header(categoriaAtual!!)
                        if (novaLista.none { it is ProdutoListItem.Header && it.categoriaNome == categoriaAtual }) {
                            novaLista.add(header)
                        }

                        novaLista.add(item)
                    }
                }
            }

            itensFiltrados = novaLista
        }

        notifyDataSetChanged()
    }

    fun getSelecionados(): List<String> = selecionados.toList()


    inner class HeaderVH(v: View) : RecyclerView.ViewHolder(v) {
        val txt: TextView = v.findViewById(R.id.txtHeaderCategoria)
    }

    inner class ItemVH(v: View) : RecyclerView.ViewHolder(v) {

        private val txtNome: TextView = v.findViewById(R.id.txtNomeProdutoCard)
        private val check: CheckBox = v.findViewById(R.id.checkSelecionarProduto)
        private val expand: ImageView = v.findViewById(R.id.btnExpandirProduto)
        private val container: LinearLayout = v.findViewById(R.id.containerDetalhesProduto)

        private val txtDescricao: TextView = v.findViewById(R.id.txtDescricaoProdutoCard)
        private val txtPreco: TextView = v.findViewById(R.id.txtPrecoProdutoCard)
        private val txtQtd: TextView = v.findViewById(R.id.txtQuantidadeProdutoCard)
        private val txtUnidade: TextView = v.findViewById(R.id.txtUnidadeProdutoCard)

        fun bind(produto: Produto) {

            txtNome.text = produto.nome
            txtDescricao.text = "Descrição: ${produto.descricao}"
            txtPreco.text = "Preço: R$ %.2f".format(produto.preco)
            txtQtd.text = "Quantidade: ${produto.quantidade}"
            txtUnidade.text = "Unidade: ${produto.unidade}"

            check.isChecked = selecionados.contains(produto.id)

            check.setOnCheckedChangeListener { _, marcado ->
                if (marcado) selecionados.add(produto.id)
                else selecionados.remove(produto.id)
                onClick(produto, marcado)
            }

            expand.setOnClickListener {
                if (container.visibility == View.GONE) {
                    container.visibility = View.VISIBLE
                    expand.setImageResource(R.drawable.flecha_cima)
                } else {
                    container.visibility = View.GONE
                    expand.setImageResource(R.drawable.flecha_baixo)
                }
            }
        }
    }
}
