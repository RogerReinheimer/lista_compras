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
    private val listas = ArrayList<Lista>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listas_salvas)

        recyclerView = findViewById(R.id.recyclerListasSalvas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListasSalvasAdapter(listas) { listaSelecionada ->
            val intent = Intent(this, ListaCriar::class.java)
            intent.putExtra("listaId", listaSelecionada.id)
            startActivity(intent)
        }
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
                            lista.id = doc.id  // importante para abrir depois
                            listas.add(lista)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }
}

