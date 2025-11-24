package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listacompras.Adapter.ProdutosListaAdapter
import com.example.listacompras.Model.Produto
import com.example.listacompras.R
import com.example.listacompras.databinding.ActivityListaEditarBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ListaEditar : AppCompatActivity() {

    private lateinit var binding: ActivityListaEditarBinding
    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView
    private val db = FirebaseFirestore.getInstance()

    private var listaId: String = ""
    private val produtos = mutableListOf<Produto>()
    private lateinit var adapter: ProdutosListaAdapter

    private val REQUEST_SELECT = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityListaEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listaId = intent.getStringExtra("listaId") ?: ""

        if (listaId.isEmpty()) {
            Toast.makeText(this, "Erro ao carregar lista", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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

        configurarRecycler()
        carregarLista()
        carregarProdutos()
        configurarBotoes()
    }

    private fun configurarRecycler() {
        adapter = ProdutosListaAdapter(
            produtos,
            onRemover = { produtoRemovido ->
                removerProdutoDoBanco(produtoRemovido.id)
                atualizarTotal()
            },
            onAtualizarTotal = { atualizarTotal() }
        )

        binding.recyclerProdutosLista.layoutManager = LinearLayoutManager(this)
        binding.recyclerProdutosLista.adapter = adapter
    }

    private fun carregarLista() {
        db.collection("listas").document(listaId).get()
            .addOnSuccessListener { doc ->
                binding.edtNomeLista.setText(doc.getString("nome") ?: "")
            }
    }

    private fun carregarProdutos() {
        db.collection("listas")
            .document(listaId)
            .collection("produtos")
            .get()
            .addOnSuccessListener { snap ->

                produtos.clear()

                for (doc in snap) {
                    produtos.add(
                        Produto(
                            id = doc.id,
                            nome = doc.getString("nome") ?: "",
                            descricao = "",
                            quantidade = doc.getDouble("quantidade") ?: 0.0,
                            unidade = doc.getString("unidade") ?: "",
                            preco = doc.getDouble("preco") ?: 0.0,
                            categoriaId = "",
                            categoriaNome = "",
                            selecionado = doc.getBoolean("selecionado") ?: false
                        )
                    )
                }

                adapter.notifyDataSetChanged()
                atualizarTotal()
            }
    }

    private fun removerProdutoDoBanco(produtoId: String) {
        db.collection("listas")
            .document(listaId)
            .collection("produtos")
            .document(produtoId)
            .delete()
    }

    private fun atualizarTotal() {
        val total = produtos.sumOf { it.preco * it.quantidade }
        binding.txtTotalLista.text = "R$ %.2f".format(total)

        // Salvar total atualizado no banco
        db.collection("listas")
            .document(listaId)
            .update("total", total)
    }

    private fun configurarBotoes() {

        // ADICIONAR PRODUTOS (IGUAL A LISTA CRIAR)
        binding.btnAddProduto.setOnClickListener {
            val intent = Intent(this, ProdutosSelecionar::class.java)

            intent.putExtra(
                "produtosAtuaisIds",
                produtos.map { it.id }.toTypedArray()
            )

            startActivityForResult(intent, REQUEST_SELECT)
        }

        // SALVAR LISTA
        binding.btnSalvarEdicao.setOnClickListener {

            val nome = binding.edtNomeLista.text.toString().trim()
            val total = produtos.sumOf { it.preco * it.quantidade }

            // Atualiza dados principais da lista
            val listaRef = db.collection("listas").document(listaId)
            listaRef.update(
                mapOf(
                    "nome" to nome,
                    "total" to total,
                    "dataAtualizacao" to FieldValue.serverTimestamp()
                )
            )

            // Atualiza todos os produtos da lista
            val produtosRef = listaRef.collection("produtos")

            produtos.forEach { p ->

                val dadosProduto = mapOf(
                    "nome" to p.nome,
                    "descricao" to p.descricao,
                    "quantidade" to p.quantidade,
                    "unidade" to p.unidade,
                    "preco" to p.preco,
                    "categoriaId" to p.categoriaId,
                    "categoriaNome" to p.categoriaNome,
                    "selecionado" to p.selecionado
                )

                produtosRef.document(p.id).set(dadosProduto)
            }

            Toast.makeText(this, "Lista atualizada!", Toast.LENGTH_SHORT).show()
            finish()
        }

        // CANCELAR
        binding.btnCancelarEdicao.setOnClickListener { finish() }

        // EXCLUIR LISTA
        binding.btnExcluirLista.setOnClickListener {
            db.collection("listas")
                .document(listaId)
                .delete()
                .addOnSuccessListener {

                    db.collection("listas")
                        .document(listaId)
                        .collection("produtos")
                        .get()
                        .addOnSuccessListener { snap ->
                            snap.forEach { it.reference.delete() }
                        }

                    Toast.makeText(this, "Lista excluída!", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT && resultCode == RESULT_OK) {

            val selecionados =
                data?.getParcelableArrayExtra("produtosSelecionados")
                    ?.map { it as Produto } ?: emptyList()

            // substitui tudo pelos novos
            produtos.clear()
            produtos.addAll(selecionados)

            adapter.notifyDataSetChanged()
            atualizarTotal()
        }
    }
}
