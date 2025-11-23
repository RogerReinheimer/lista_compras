package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class CategoriaEditar : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView
    private lateinit var edtNome: EditText
    private lateinit var btnSalvar: AppCompatButton
    private lateinit var btnCancelar: AppCompatButton
    private lateinit var btnExcluir: AppCompatButton

    private var categoriaId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_categoria_editar)

        iconHome = findViewById(R.id.iconHome)
        iconListas = findViewById(R.id.iconListas)
        iconCategorias = findViewById(R.id.iconCategorias)
        iconProdutos = findViewById(R.id.iconProdutos)

        iconHome.setOnClickListener {
            startActivity(Intent(this, TelaHome::class.java))
        }

        /*
        iconListas.setOnClickListener {
            startActivity(Intent(this, TelaListas::class.java))
        }
        */

        iconCategorias.setOnClickListener {
            startActivity(Intent(this, CategoriasSalvas::class.java))
        }

        iconProdutos.setOnClickListener {
            startActivity(Intent(this, ProdutoSalvos::class.java))
        }

        edtNome = findViewById(R.id.edtNomeCategoriaEditar)
        btnSalvar = findViewById(R.id.btnSalvarEdicao)
        btnCancelar = findViewById(R.id.btnCancelarEdicao)
        btnExcluir = findViewById(R.id.btnExcluirCategoria)

        // PEGAR DADOS VINDOS DO ADAPTER
        categoriaId = intent.getStringExtra("id")
        val nomeRecebido = intent.getStringExtra("nome")

        edtNome.setText(nomeRecebido)

        // CANCELAR
        btnCancelar.setOnClickListener { finish() }

        // SALVAR
        btnSalvar.setOnClickListener {
            val novoNome = edtNome.text.toString().trim()

            if (novoNome.isEmpty()) {
                Toast.makeText(this, "Digite o nome da categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (categoriaId == null) {
                Toast.makeText(this, "Erro: ID da categoria não recebido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("categorias")
                .document(categoriaId!!)
                .update("nome", novoNome)
                .addOnSuccessListener {
                    Toast.makeText(this, "Categoria atualizada!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // EXCLUIR
        btnExcluir.setOnClickListener {
            if (categoriaId == null) {
                Toast.makeText(this, "Erro: ID não encontrado!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Excluir categoria")
                .setMessage("Tem certeza que deseja excluir esta categoria?")
                .setPositiveButton("Excluir") { _, _ ->
                    db.collection("categorias")
                        .document(categoriaId!!)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Categoria excluída!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
