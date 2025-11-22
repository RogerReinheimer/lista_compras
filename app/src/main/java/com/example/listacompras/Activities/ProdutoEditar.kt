package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.example.listacompras.R
import com.google.firebase.firestore.FirebaseFirestore

class ProdutoEditar : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var edtNome: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var edtPreco: EditText
    private lateinit var edtQuantidade: EditText

    private lateinit var spnUnidade: Spinner
    private lateinit var spnCategoria: Spinner

    private lateinit var btnSalvar: AppCompatButton
    private lateinit var btnCancelar: AppCompatButton
    private lateinit var btnExcluir: AppCompatButton

    private var produtoId: String? = null
    private var categoriaIdAtual: String? = null
    private var categoriaNomeAtual: String? = null
    private var categoriasIds = mutableListOf<String>()
    private var categoriasNomes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_produtos_editar)

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

        // CAMPOS
        edtNome = findViewById(R.id.edtNomeProdutoEd)
        edtDescricao = findViewById(R.id.edtDescricaoProdutoEd)
        edtPreco = findViewById(R.id.edtPrecoProdutoEd)
        edtQuantidade = findViewById(R.id.edtQuantidadeProdutoEd)

        spnUnidade = findViewById(R.id.spinnerUnidadeProdutoEd)
        spnCategoria = findViewById(R.id.spinnerCategoriaProduto)

        btnSalvar = findViewById(R.id.btnSalvarEdicaoProduto)
        btnCancelar = findViewById(R.id.btnCancelarEdicaoProduto)
        btnExcluir = findViewById(R.id.btnExcluirProduto)

        // PEGAR DADOS
        produtoId = intent.getStringExtra("id")
        categoriaIdAtual = intent.getStringExtra("categoriaId")
        categoriaNomeAtual = intent.getStringExtra("categoriaNome")

        val nome = intent.getStringExtra("nome").orEmpty()
        val descricao = intent.getStringExtra("descricao").orEmpty()
        val preco = intent.getDoubleExtra("preco", 0.0)
        val quantidade = intent.getDoubleExtra("quantidade", 0.0)
        val unidade = intent.getStringExtra("unidade").orEmpty()

        edtNome.setText(nome)
        edtDescricao.setText(descricao)
        edtPreco.setText(preco.toString())
        edtQuantidade.setText(quantidade.toString())

        // UNIDADE
        val unidades = listOf("Un", "Dz", "Ml", "L", "Kg", "G", "Caixa", "Embalagem", "Galão", "Garrafa", "Lata", "Pacote")
        spnUnidade.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        spnUnidade.setSelection(unidades.indexOf(unidade))

        // CARREGAR CATEGORIAS
        carregarCategorias()

        // AÇÕES
        btnCancelar.setOnClickListener { finish() }
        btnSalvar.setOnClickListener { salvarProduto() }
        btnExcluir.setOnClickListener { excluirProduto() }
    }

    private fun carregarCategorias() {
        db.collection("categorias")
            .get()
            .addOnSuccessListener { result ->

                categoriasIds.clear()
                categoriasNomes.clear()

                for (doc in result) {
                    categoriasNomes.add(doc.getString("nome") ?: "")
                    categoriasIds.add(doc.id)
                }

                spnCategoria.adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    categoriasNomes
                )

                // Seleciona categoria atual
                val pos = categoriasNomes.indexOf(categoriaNomeAtual)
                if (pos >= 0) spnCategoria.setSelection(pos)
            }
    }

    private fun salvarProduto() {
        if (produtoId == null) {
            Toast.makeText(this, "Erro: ID do produto não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val novaCategoriaId = categoriasIds[spnCategoria.selectedItemPosition]
        val novaCategoriaNome = categoriasNomes[spnCategoria.selectedItemPosition]

        val dadosAtualizados = mapOf(
            "nome" to edtNome.text.toString(),
            "descricao" to edtDescricao.text.toString(),
            "preco" to edtPreco.text.toString().toDoubleOrNull(),
            "quantidade" to edtQuantidade.text.toString().toDoubleOrNull(),
            "unidade" to spnUnidade.selectedItem.toString(),
            "categoriaId" to novaCategoriaId,
            "categoriaNome" to novaCategoriaNome
        )

        // Atualiza diretamente na coleção "produtos"
        db.collection("produtos")
            .document(produtoId!!)
            .update(dadosAtualizados)
            .addOnSuccessListener {
                Toast.makeText(this, "Produto atualizado!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirProduto() {
        if (produtoId == null) {
            Toast.makeText(this, "Erro: ID do produto não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Excluir produto")
            .setMessage("Tem certeza que deseja excluir este produto?")
            .setPositiveButton("Excluir") { _, _ ->
                db.collection("produtos")
                    .document(produtoId!!)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Produto excluído!", Toast.LENGTH_SHORT).show()
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
