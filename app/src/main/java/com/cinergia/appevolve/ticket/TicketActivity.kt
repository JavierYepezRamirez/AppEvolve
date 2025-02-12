package com.cinergia.appevolve.ticket

import Clientes
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R

class TicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket2)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets }

    val cliente = intent.getSerializableExtra("cliente")as? Clientes ?: Clientes()
    val usuario:String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()
    val fecha:String = intent.extras?.getString("fecha").orEmpty()
    val credito:String = intent.extras?.getString("credito").orEmpty()
    val observaciones:String = intent.extras?.getString("observaciones").orEmpty()
    val pago: Int = intent.extras?.getInt("pago", 0) ?: 0

    findViewById<TextView>(R.id.tvNombre).text = "Nombre: ${cliente.nombre ?: "Desconocido"}"
    findViewById<TextView>(R.id.tvTelefono).text = "Teléfono: ${cliente.telefono ?: "Desconocido"}"
    findViewById<TextView>(R.id.tvDireccion).text = "Dirección: ${cliente.direccion ?: "Desconocido"}"
    findViewById<TextView>(R.id.tvPlan).text = "Plan($): ${cliente.plan ?: "Desconocido"}"
    findViewById<TextView>(R.id.tvFecha).text = "Fecha: $fecha"


    }
}

