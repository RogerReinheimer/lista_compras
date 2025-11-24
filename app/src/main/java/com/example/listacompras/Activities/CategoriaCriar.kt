package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.example.listacompras.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoriaCriar : AppCompatActivity() {

    private val db = Firebase.firestore

    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_categoria_criar)

        val edtNome = findViewById<EditText>(R.id.edtNomeCategoria)
        val btnSalvar = findViewById<AppCompatButton>(R.id.btnSalvar)
        val btnCancelar = findViewById<AppCompatButton>(R.id.btnCancelar)

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

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString().trim()

            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite um nome!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoria = hashMapOf("nome" to nome)

            db.collection("categorias")
                .add(categoria)
                .addOnSuccessListener {
                    Toast.makeText(this, "Categoria criada!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CategoriasSalvas::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
