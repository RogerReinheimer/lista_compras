package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Adapter.CategoriasAdapter
import com.example.listacompras.Model.Categoria
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class Categorias : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoriasAdapter
    private val listaCategorias = mutableListOf<Categoria>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_categorias)

        recyclerView = findViewById(R.id.recyclerCategorias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CategoriasAdapter(listaCategorias) { categoria ->
            val intent = Intent(this, EditarCategoria::class.java)
            intent.putExtra("id", categoria.id)
            intent.putExtra("nome", categoria.nome)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        val btnAdd = findViewById<ImageView>(R.id.btnAdicionarCategoria)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, CriarCategoria::class.java))
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
