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
        val nome: TextView = itemView.findViewById(R.id.txtNomeProdutoCard)
        val descricao: TextView = itemView.findViewById(R.id.txtDescricaoProdutoCard)

        val txtPreco: TextView = itemView.findViewById(R.id.txtPrecoProdutoCard)
        val txtQuantidade: TextView = itemView.findViewById(R.id.txtQuantidadeProdutoCard)
        val txtUnidade: TextView = itemView.findViewById(R.id.txtUnidadeProdutoCard)

        val btnRemover: ImageView = itemView.findViewById(R.id.btnRemoverProdutoCard)
        val btnExpandir: ImageView = itemView.findViewById(R.id.btnExpandirProduto)
        val container: LinearLayout = itemView.findViewById(R.id.containerDetalhesProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto_lista, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = produtos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // importante pegar produto por posição no momento do bind
        val p = produtos[position]

        holder.nome.text = p.nome
        holder.descricao.text = "Descrição: ${p.descricao}"

        holder.txtPreco.text = "Preço: R$ %.2f".format(p.preco)
        holder.txtQuantidade.text = "Quantidade: ${p.quantidade}"
        holder.txtUnidade.text = "Unidade: ${p.unidade}"

        // EXPANDIR / COLAPSAR (mantém estado visual independente)
        holder.btnExpandir.setOnClickListener {
            if (holder.container.visibility == View.GONE) {
                holder.container.visibility = View.VISIBLE
                holder.btnExpandir.setImageResource(R.drawable.flecha_cima)
            } else {
                holder.container.visibility = View.GONE
                holder.btnExpandir.setImageResource(R.drawable.flecha_baixo)
            }
        }

        // REMOVER → delega a remoção para a Activity via callback.
        // A Activity fará a remoção no dataset e notificará o adapter.
        holder.btnRemover.setOnClickListener {
            onRemover(p)
        }
    }
}
