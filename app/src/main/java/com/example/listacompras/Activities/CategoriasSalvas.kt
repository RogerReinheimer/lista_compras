package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Adapter.CategoriasAdapter
import com.example.listacompras.Model.Categoria
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasSalvas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoriasAdapter
    private val listaCategorias = mutableListOf<Categoria>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_categorias_salvas)

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

        recyclerView = findViewById(R.id.recyclerCategorias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoriasAdapter(listaCategorias) { categoria ->
            val intent = Intent(this, CategoriaEditar::class.java)
            intent.putExtra("id", categoria.id)
            intent.putExtra("nome", categoria.nome)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        val btnAdd = findViewById<ImageView>(R.id.btnAdicionarCategoria)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, CategoriaCriar::class.java))
        }

        carregarCategorias()
    }

    override fun onResume() {
        super.onResume()
        carregarCategorias()
    }

    private fun carregarCategorias() {
        db.collection("categorias")
            .orderBy("nome")
            .get()
            .addOnSuccessListener { result ->
                listaCategorias.clear()

                for (doc in result) {
                    val categoria = Categoria(
                        id = doc.id,
                        nome = doc.getString("nome") ?: ""
                    )
                    listaCategorias.add(categoria)
                }

                adapter.notifyDataSetChanged() // Atualiza a lista
            }
    }
}
