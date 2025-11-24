package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Adapter.SelecionarProdutosAdapter
import com.example.listacompras.Model.Produto
import com.example.listacompras.Model.ProdutoListItem
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class ProdutosSelecionar : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: SelecionarProdutosAdapter

    private val listaItens = mutableListOf<ProdutoListItem>()
    private val selecionadosIds = mutableSetOf<String>()
    private val selecionadosObj = mutableListOf<Produto>()
    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_produtos_selecionar)

        iconHome = findViewById(R.id.iconHome)
        iconListas = findViewById(R.id.iconListas)
        iconCategorias = findViewById(R.id.iconCategorias)
        iconProdutos = findViewById(R.id.iconProdutos)

        iconHome.setOnClickListener {
            startActivity(Intent(this, TelaHome::class.java))
        }

        iconListas.setOnClickListener {
            startActivity(Intent(this, ListasSalvas::class.java))
        }

        iconCategorias.setOnClickListener {
            startActivity(Intent(this, CategoriasSalvas::class.java))
        }

        iconProdutos.setOnClickListener {
            startActivity(Intent(this, ProdutoSalvos::class.java))
        }

        recycler = findViewById(R.id.recyclerSelecionarProdutos)
        recycler.layoutManager = LinearLayoutManager(this)

        val btnCancelar = findViewById<TextView>(R.id.btnCancelar)
        val btnAdicionar = findViewById<TextView>(R.id.btnAdicionar)

        btnCancelar.setOnClickListener { finish() }
        btnAdicionar.setOnClickListener { finishWithResult() }

        onBackPressedDispatcher.addCallback(this) { finish() }

        carregarProdutos()
    }

    private fun carregarProdutos() {
        db.collection("produtos")
            .orderBy("categoriaNome")
            .get()
            .addOnSuccessListener { result ->

                val produtos = result.map { doc ->
                    Produto(
                        id = doc.id,
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

        adapter = SelecionarProdutosAdapter(
            listaItens,
            selecionadosIds
        ) { produto, marcado ->

            if (marcado)
                selecionadosObj.add(produto)
            else
                selecionadosObj.remove(produto)
        }

        recycler.adapter = adapter
    }

    private fun finishWithResult() {
        val intent = Intent()
        intent.putExtra("produtosSelecionados", selecionadosObj.toTypedArray())
        setResult(RESULT_OK, intent)
        finish()
    }
}
