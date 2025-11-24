package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
    private lateinit var searchBox: EditText

    private val listaItens = mutableListOf<ProdutoListItem>()
    private val selecionadosIds = mutableSetOf<String>()
    private val selecionadosObj = mutableListOf<Produto>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_produtos_selecionar)

        recycler = findViewById(R.id.recyclerSelecionarProdutos)
        recycler.layoutManager = LinearLayoutManager(this)

        searchBox = findViewById(R.id.edtBuscarProdutoSelecao)

        val recebidos = intent.getStringArrayExtra("produtosAtuaisIds")
        recebidos?.let { selecionadosIds.addAll(it) }

        carregarProdutos()

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filtrar(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val btnCancelar = findViewById<TextView>(R.id.btnCancelar)
        val btnAdicionar = findViewById<TextView>(R.id.btnAdicionar)

        btnCancelar.setOnClickListener { finish() }
        btnAdicionar.setOnClickListener { finishWithResult() }

        onBackPressedDispatcher.addCallback(this) { finish() }
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
        selecionadosObj.clear()

        var categoriaAtual = ""

        produtos.sortedBy { it.categoriaNome }.forEach { produto ->

            if (produto.categoriaNome != categoriaAtual) {
                categoriaAtual = produto.categoriaNome
                listaItens.add(ProdutoListItem.Header(categoriaAtual))
            }

            listaItens.add(ProdutoListItem.Item(produto))

            if (selecionadosIds.contains(produto.id)) {
                selecionadosObj.add(produto)
            }
        }

        adapter = SelecionarProdutosAdapter(
            listaItens.toList(),
            selecionadosIds
        ) { produto, marcado ->
            if (marcado) selecionadosObj.add(produto)
            else selecionadosObj.remove(produto)
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
