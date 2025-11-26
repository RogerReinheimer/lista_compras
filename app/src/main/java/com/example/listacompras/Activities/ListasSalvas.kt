package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Adapter.ListasSalvasAdapter
import com.example.listacompras.Model.Lista
import com.example.listacompras.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ListasSalvas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListasSalvasAdapter
    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView

    private val listas = ArrayList<Lista>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_listas_salvas)

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

        recyclerView = findViewById(R.id.recyclerListasSalvas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // CALLBACK ABRIR + EDITAR
        adapter = ListasSalvasAdapter(
            listas,
            onClick = { /* não faz nada */ },
            onEditarClick = { listaId ->
                val intent = Intent(this, ListaEditar::class.java)
                intent.putExtra("listaId", listaId)
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        carregarListas()

        val btnAdd = findViewById<ImageView>(R.id.btnAdicionarLista)
        btnAdd.setOnClickListener {
            startActivity(Intent(this, ListaCriar::class.java))
        }
    }

    private fun carregarListas() {
        db.collection("listas")
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->

                listas.clear()

                if (snap != null) {
                    for (doc in snap.documents) {
                        val lista = doc.toObject(Lista::class.java)
                        if (lista != null) {
                            lista.id = doc.id
                            listas.add(lista)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }
}
