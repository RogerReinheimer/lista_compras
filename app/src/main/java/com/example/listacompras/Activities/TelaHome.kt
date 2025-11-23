package com.example.listacompras.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listacompras.R

class TelaHome : AppCompatActivity() {

    private lateinit var btnVerListas: LinearLayout
    private lateinit var btnCategorias: LinearLayout
    private lateinit var btnProdutosSalvos: LinearLayout

    private lateinit var iconHome: ImageView
    private lateinit var iconListas: ImageView
    private lateinit var iconCategorias: ImageView
    private lateinit var iconProdutos: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnVerListas = findViewById(R.id.btnVerListas)
        btnCategorias = findViewById(R.id.btnCategorias)
        btnProdutosSalvos = findViewById(R.id.btnProdutosSalvos)

        iconHome = findViewById(R.id.iconHome)
        iconListas = findViewById(R.id.iconListas)
        iconCategorias = findViewById(R.id.iconCategorias)
        iconProdutos = findViewById(R.id.iconProdutos)


        btnVerListas.setOnClickListener {
            startActivity(Intent(this, ListasSalvas::class.java))
        }


        btnCategorias.setOnClickListener {
            startActivity(Intent(this, CategoriasSalvas::class.java))
        }

        btnProdutosSalvos.setOnClickListener {
            startActivity(Intent(this, ProdutoSalvos::class.java))
        }

        //ICONES DO RODAPÉ
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
    }
}
