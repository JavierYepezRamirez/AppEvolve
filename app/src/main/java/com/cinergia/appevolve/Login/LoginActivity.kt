package com.cinergia.appevolve.Login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val etContrasena = findViewById<AppCompatEditText>(R.id.etContrasena)
        val btnTogglePassword = findViewById<ImageView>(R.id.btnTogglePassword)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        val usuario: String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()
        tvBienvenida.text = "Bienvenido $usuario"

        btnTogglePassword.setOnClickListener {
            if (etContrasena.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                etContrasena.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_visibility_off)
            } else {
                etContrasena.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_visibility_on)
            }
            etContrasena.setSelection(etContrasena.text?.length ?: 0)
        }

        btnEntrar.setOnClickListener {
            val contrasena = etContrasena.text.toString()

            when (usuario) {
                "Usuario1" -> navigateInicioUsuario1(usuario, contrasena)
                "Usuario2" -> navigateInicioUsuario2(usuario, contrasena)
            }
        }
    }

    private fun navigateInicioUsuario1(usuario: String, contrasena: String) {
        if (contrasena == "123456") {
            Log.e("MiTag", "Contraseña correcta para $usuario")
            //val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("EXTRA_USUARIO",usuario)
            startActivity(intent)
        } else {
            Log.e("MiTag", "Error en contraseña de $usuario")
            //Mostrar un mensaje de error
        }
    }

    private fun navigateInicioUsuario2(usuario: String, contrasena: String) {
        if (contrasena == "654321") {
            Log.e("MiTag", "Contraseña correcta para $usuario")
            //val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("EXTRA_USUARIO",usuario)
            startActivity(intent)
        } else {
            Log.e("MiTag", "Error en contraseña de $usuario")
            //Mostrar un mensaje de error
        }
    }
}