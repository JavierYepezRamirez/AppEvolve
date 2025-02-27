package com.cinergia.appevolve.Ticket

import Clientes
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R
import com.cinergia.appevolve.main.MainActivity

class CancelarPagoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cancelar_pago)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cliente = intent.getSerializableExtra("cliente")as? Clientes ?: Clientes()
        val usuario:String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()
        val fecha:String = intent.extras?.getString("fecha").orEmpty()
        val observaciones:String = intent.extras?.getString("observaciones").orEmpty()
        val pago: Int = intent.extras?.getInt("pago", 0) ?: 0

        findViewById<TextView>(R.id.tvNombrec).text = "Nombre: ${cliente.nombre ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvTelefonoc).text = "Teléfono: ${cliente.telefono ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvPlanc).text = "Plan($): ${cliente.plan ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvFechac).text = "Fecha: $fecha"

        val btnYes = findViewById<Button>(R.id.btnYesc)

        btnYes.setOnClickListener { goToMain(usuario) }
    }

    private fun goToMain(usuario: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("EXTRA_USUARIO", usuario)
        startActivity(intent)
        finish()
    }
}