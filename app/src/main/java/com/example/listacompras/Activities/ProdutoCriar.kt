package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import com.example.listacompras.Model.Produto
import com.example.listacompras.Model.Categoria
import com.example.listacompras.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProdutoCriar : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView
    private lateinit var edtNome: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var edtQuantidade: EditText
    private lateinit var edtPreco: EditText
    private lateinit var spinnerUnidade: Spinner
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnSalvar: AppCompatButton
    private lateinit var btnCancelar: AppCompatButton
    private var listaCategorias: MutableList<Categoria> = mutableListOf()
    private var listaNomesCategorias: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_produto_criar)

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

        // IDs dos componentes
        edtNome = findViewById(R.id.edtNome)
        edtDescricao = findViewById(R.id.edtDescricaoProduto)
        edtQuantidade = findViewById(R.id.edtQuantidadeProduto)
        edtPreco = findViewById(R.id.edtPrecoProduto)

        spinnerUnidade = findViewById(R.id.spinnerUnidade)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaProduto)

        btnSalvar = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Carregar listas do spinner
        carregarUnidades()
        carregarCategorias()

        // Botão salvar
        btnSalvar.setOnClickListener { salvarProduto() }

        // Botão cancelar
        btnCancelar.setOnClickListener { finish() }
    }

    // ------------------------
    // CARREGAR UNIDADES
    // ------------------------
    private fun carregarUnidades() {
        val unidades = listOf("Un", "Dz", "Ml", "L", "Kg", "G", "Caixa", "Embalagem", "Galão", "Garrafa", "Lata", "Pacote")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        spinnerUnidade.adapter = adapter
    }

    // ------------------------
    // CARREGAR CATEGORIAS DO FIRESTORE
    // ------------------------
    private fun carregarCategorias() {
        db.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                listaCategorias.clear()
                listaNomesCategorias.clear()

                for (doc in result) {
                    val categoria = Categoria(
                        id = doc.id,
                        nome = doc.getString("nome") ?: ""
                    )
                    listaCategorias.add(categoria)
                    listaNomesCategorias.add(categoria.nome)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listaNomesCategorias
                )
                spinnerCategoria.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
            }
    }

    // ------------------------
    // SALVAR PRODUTO NO FIRESTORE
    // ------------------------
    private fun salvarProduto() {
        val nome = edtNome.text.toString().trim()
        val descricao = edtDescricao.text.toString().trim()
        val quantidadeTxt = edtQuantidade.text.toString().trim()
        val precoTxt = edtPreco.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite o nome!", Toast.LENGTH_SHORT).show()
            return
        }
        if (quantidadeTxt.isEmpty()) {
            Toast.makeText(this, "Digite a quantidade!", Toast.LENGTH_SHORT).show()
            return
        }
        if (precoTxt.isEmpty()) {
            Toast.makeText(this, "Digite o preço!", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = quantidadeTxt.toDoubleOrNull() ?: 0.0
        val preco = precoTxt.toDoubleOrNull() ?: 0.0

        val indexCategoria = spinnerCategoria.selectedItemPosition
        val categoriaSelecionada = listaCategorias[indexCategoria]
        val unidadeSelecionada = spinnerUnidade.selectedItem.toString()

        // ⚡ Cria novo documento com ID automático do Firestore
        val novoDoc = db.collection("produtos").document()

        val produto = Produto(
            id = novoDoc.id, // ⚡ ID do Firestore
            nome = nome,
            descricao = descricao,
            quantidade = quantidade,
            unidade = unidadeSelecionada,
            preco = preco,
            categoriaId = categoriaSelecionada.id,
            categoriaNome = categoriaSelecionada.nome
        )

        // Salvar no Firestore
        novoDoc.set(produto)
            .addOnSuccessListener {
                Toast.makeText(this, "Produto criado!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
