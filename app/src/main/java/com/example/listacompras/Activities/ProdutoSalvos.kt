package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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

    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProdutosAdapter
    private val listaItens = mutableListOf<ProdutoListItem>()

    private lateinit var searchBox: EditText

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_produtos_salvos)

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

        recycler = findViewById(R.id.recyclerProdutosSalvos)
        recycler.layoutManager = LinearLayoutManager(this)

        searchBox = findViewById(R.id.edtBuscarProduto)

        val btnAdd = findViewById<ImageView>(R.id.btnAdicionarProduto)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, ProdutoCriar::class.java))
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filtrar(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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

        adapter = ProdutosAdapter(listaItens.toList(), this)
        recycler.adapter = adapter
    }
}
