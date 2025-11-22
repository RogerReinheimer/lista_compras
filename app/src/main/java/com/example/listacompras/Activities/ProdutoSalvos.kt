package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
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

        val btnVerListas = findViewById<LinearLayout>(R.id.btnVerListas)
        val btnCategorias = findViewById<LinearLayout>(R.id.btnCategorias)
        val btnProdutosSalvos = findViewById<LinearLayout>(R.id.btnProdutosSalvos)

        val iconHome = findViewById<ImageView>(R.id.iconHome)
        val iconListas = findViewById<ImageView>(R.id.iconListas)
        val iconCategorias = findViewById<ImageView>(R.id.iconCategorias)
        val iconProdutos = findViewById<ImageView>(R.id.iconProdutos)

        // CARDS DA TELA
        /*
        // tela de lista
        btnVerListas.setOnClickListener {
            val intent = Intent(this, TelaListas::class.java)
            startActivity(intent)
        }*/
        // tela das categorias
        btnCategorias.setOnClickListener {
            val intent = Intent(this, CategoriasSalvas::class.java)
            startActivity(intent)
        }
        // tela dos produtos
        btnProdutosSalvos.setOnClickListener {
            val intent = Intent(this, ProdutoSalvos::class.java)
            startActivity(intent)
        }

        //ICONES DO RODAPE
        //tela home
        iconHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        /*
        // tela de listas
        iconListas.setOnClickListener {
            val intent = Intent(this, TelaListas::class.java)
            startActivity(intent)
        }*/
        // tela de categorias
        iconCategorias.setOnClickListener {
            val intent = Intent(this, CategoriasSalvas::class.java)
            startActivity(intent)
        }

        // tela dos produtos
        iconProdutos.setOnClickListener {
            val intent = Intent(this, ProdutoSalvos::class.java)
            startActivity(intent)
        }

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
