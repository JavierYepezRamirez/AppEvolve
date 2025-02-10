package com.cinergia.appevolve.Inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
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
        
        btnUsuario1.setOnClickListener { navigateToLoginUsuario1() }
        btnUsuario2.setOnClickListener { navigateToLoginUsuario2() }
    }

    private fun navigateToLoginUsuario1() {
        // Cambiar Usuario1 nombre de la persona
        val usuario1 = "Usuario1"
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("EXTRA_USUARIO",usuario1)
        startActivity(intent)

    }

    private fun navigateToLoginUsuario2() {
        // Cambiar Usuario1 nombre de la persona
        val usuario2 = "Usuario2"
        val intent = Intent(this,LoginActivity::class.java)
        intent.putExtra("EXTRA_USUARIO",usuario2)
        startActivity(intent)
    }
}