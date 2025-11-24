package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listacompras.Adapter.ProdutosListaAdapter
import com.example.listacompras.Model.Produto
import com.example.listacompras.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class ListaCriar : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProdutosListaAdapter
    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView
    private lateinit var edtNomeLista: EditText
    private lateinit var txtTotal: TextView

    private val produtosSelecionados = mutableListOf<Produto>()

    private val db = Firebase.firestore

    private val REQUEST_SELECT = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_criar)

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

        edtNomeLista = findViewById(R.id.edtNomeLista)
        txtTotal = findViewById(R.id.txtTotalLista)

        recycler = findViewById(R.id.recyclerProdutosLista)

        adapter = ProdutosListaAdapter(
            produtosSelecionados,
            onRemover = { atualizarTotal() },
            onAtualizarTotal = { atualizarTotal() }
        )

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<View>(R.id.btnAddProduto).setOnClickListener {
            val intent = Intent(this, ProdutosSelecionar::class.java)

            // ENVIA IDs DOS PRODUTOS JÁ SELECIONADOS
            intent.putExtra(
                "produtosAtuaisIds",
                produtosSelecionados.map { it.id }.toTypedArray()
            )

            startActivityForResult(intent, REQUEST_SELECT)
        }

        findViewById<View>(R.id.btnCancelarLista).setOnClickListener { finish() }

        findViewById<View>(R.id.btnSalvarLista).setOnClickListener { salvarLista() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT && resultCode == RESULT_OK) {

            val recebidos =
                data?.getParcelableArrayExtra("produtosSelecionados")?.map { it as Produto }
                    ?: emptyList()

            produtosSelecionados.clear() // NÃO DUPLICA MAIS
            produtosSelecionados.addAll(recebidos)

            adapter.notifyDataSetChanged()
            atualizarTotal()
        }
    }

    private fun atualizarTotal() {
        val total = produtosSelecionados.sumOf { it.preco * it.quantidade }
        txtTotal.text = "R$ %.2f".format(total)
    }

    private fun salvarLista() {

        val nomeLista = edtNomeLista.text.toString().trim()

        if (nomeLista.isEmpty()) {
            Toast.makeText(this, "Digite o nome da lista", Toast.LENGTH_SHORT).show()
            return
        }

        val novaLista = hashMapOf(
            "nome" to nomeLista,
            "total" to produtosSelecionados.sumOf { it.preco * it.quantidade },
            "dataCriacao" to FieldValue.serverTimestamp()
        )

        db.collection("listas")
            .add(novaLista)
            .addOnSuccessListener { docRef ->

                val idLista = docRef.id

                val sub = db.collection("listas")
                    .document(idLista)
                    .collection("produtos")

                produtosSelecionados.forEach { p ->
                    val dados = hashMapOf(
                        "produtoIdRef" to p.id,
                        "nome" to p.nome,
                        "preco" to p.preco,
                        "quantidade" to p.quantidade,
                        "unidade" to p.unidade
                    )
                    sub.add(dados)
                }

                Toast.makeText(this, "Lista salva!", Toast.LENGTH_SHORT).show()
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar!", Toast.LENGTH_SHORT).show()
            }
    }
}
