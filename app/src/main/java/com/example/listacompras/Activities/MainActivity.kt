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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
    }// oncreate


}//fim codigo