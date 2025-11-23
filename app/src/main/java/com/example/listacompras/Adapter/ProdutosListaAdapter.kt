package com.example.listacompras.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Model.Produto
import com.example.listacompras.R

class ProdutosListaAdapter(
    private val produtos: MutableList<Produto>,
    private val onRemover: (Produto) -> Unit,
    private val onAtualizarTotal: () -> Unit
) : RecyclerView.Adapter<ProdutosListaAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nome = itemView.findViewById<TextView>(R.id.txtNomeProdutoCard)
        val descricao = itemView.findViewById<TextView>(R.id.txtDescricaoProdutoCard)
        val preco = itemView.findViewById<TextView>(R.id.txtPrecoProdutoCard)
        val quantidade = itemView.findViewById<TextView>(R.id.txtQuantidadeProdutoCard)
        val unidade = itemView.findViewById<TextView>(R.id.txtUnidadeProdutoCard)

        val btnRemover = itemView.findViewById<ImageView>(R.id.btnRemoverProdutoCard)
        val btnExpandir = itemView.findViewById<ImageView>(R.id.btnExpandirProduto)
        val container = itemView.findViewById<LinearLayout>(R.id.containerDetalhesProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto_lista, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = produtos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val p = produtos[position]

        holder.nome.text = p.nome
        holder.descricao.text = "Descrição: ${p.descricao}"
        holder.preco.text = "Preço: R$ ${"%.2f".format(p.preco)}"
        holder.quantidade.text = "Quantidade: ${p.quantidade}"
        holder.unidade.text = "Unidade: ${p.unidade}"

        // EXPANDIR/COLAPSAR
        holder.btnExpandir.setOnClickListener {
            if (holder.container.visibility == View.GONE) {
                holder.container.visibility = View.VISIBLE
                holder.btnExpandir.setImageResource(R.drawable.flecha_cima)
            } else {
                holder.container.visibility = View.GONE
                holder.btnExpandir.setImageResource(R.drawable.flecha_baixo)
            }
        }

        // REMOVER PRODUTO
        holder.btnRemover.setOnClickListener {
            val removido = produtos[position]
            produtos.removeAt(position)
            notifyItemRemoved(position)
            onRemover(removido)
            onAtualizarTotal()
        }
    }
}
