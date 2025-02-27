package com.cinergia.appevolve.Inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.Login.LoginActivity
import com.cinergia.appevolve.R

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnUsuario1 = findViewById<Button>(R.id.btnUsuario1)
        val btnUsuario2 = findViewById<Button>(R.id.btnUsuario2)
        val btnUsuario3 = findViewById<Button>(R.id.btnUsuario3)
        val btnInfo = findViewById<ImageView>(R.id.btnInfo)

        btnUsuario1.setOnClickListener { navigateToLoginUsuario1() }
        btnUsuario2.setOnClickListener { navigateToLoginUsuario2() }
        btnUsuario3.setOnClickListener { navigateToLoginUsuario3() }
        btnInfo.setOnClickListener { mostrarDialogoInformacion() }
    }

    private fun navigateToLoginUsuario1() {
        // Cambiar Usuario1 nombre de la persona
        val usuario1 = "Socorro"
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("EXTRA_USUARIO",usuario1)
        startActivity(intent)

    }

    private fun navigateToLoginUsuario2() {
        // Cambiar Usuario1 nombre de la persona
        val usuario2 = "Martha"
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("EXTRA_USUARIO",usuario2)
        startActivity(intent)
    }

    private fun navigateToLoginUsuario3() {
        val usuario3 = "EvolveAdmin"
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("EXTRA_USUARIO",usuario3)
        startActivity(intent)
    }

    private fun mostrarDialogoInformacion() {
        AlertDialog.Builder(this)
            .setTitle("Información del Programa")
            .setMessage("Este programa fue desarrollado por María Luisa Aguilar Pérez y Javier Yépez Ramírez, con la supervisión y apoyo de: Lesli Yared Villegas Fonseca y Juanita del Carmen Mendoza Ornelas.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}