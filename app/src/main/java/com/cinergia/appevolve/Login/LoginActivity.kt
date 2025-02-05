package com.cinergia.appevolve.Login

import android.os.Bundle
import android.text.InputType
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
        val usuario: String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()
        val etContrasena = findViewById<AppCompatEditText>(R.id.etContrasena)
        val btnTogglePassword = findViewById<ImageView>(R.id.btnTogglePassword)

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

    }
}