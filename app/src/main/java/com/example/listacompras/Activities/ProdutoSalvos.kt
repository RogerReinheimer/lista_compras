package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Adapter.ProdutosAdapter
import com.example.listacompras.Model.Produto
import com.example.listacompras.Model.ProdutoListItem
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class ProdutoSalvos : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProdutosAdapter
    private val listaItens = mutableListOf<ProdutoListItem>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_produtos_salvos)

        recycler = findViewById(R.id.recyclerProdutosSalvos)
        recycler.layoutManager = LinearLayoutManager(this)

        val btnAdd = findViewById<ImageView>(R.id.btnAdicionarProduto)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, ProdutoCriar::class.java))
        }

        carregarProdutos()
    }

    override fun onResume() {
        super.onResume()
        carregarProdutos()
    }

    private fun carregarProdutos() {
        db.collection("produtos")
            .orderBy("categoriaNome")
            .get()
            .addOnSuccessListener { result ->

                val produtos = result.map { doc ->
                    Produto(
                        id = doc.id, // ⚡ usa o ID do Firestore, não um campo interno
                        nome = doc.getString("nome") ?: "",
                        descricao = doc.getString("descricao") ?: "",
                        quantidade = doc.getDouble("quantidade") ?: 0.0,
                        unidade = doc.getString("unidade") ?: "",
                        preco = doc.getDouble("preco") ?: 0.0,
                        categoriaId = doc.getString("categoriaId") ?: "",
                        categoriaNome = doc.getString("categoriaNome") ?: ""
                    )
                }

                montarLista(produtos)
            }
    }

    private fun montarLista(produtos: List<Produto>) {
        listaItens.clear()

        var categoriaAtual = ""

        produtos.sortedBy { it.categoriaNome }.forEach { produto ->
            if (produto.categoriaNome != categoriaAtual) {
                categoriaAtual = produto.categoriaNome
                listaItens.add(ProdutoListItem.Header(categoriaAtual))
            }

            listaItens.add(ProdutoListItem.Item(produto))
        }

        adapter = ProdutosAdapter(
            listaItens,
            this,
            ""
        )

        recycler.adapter = adapter
    }
}
