package com.cinergia.appevolve.ticket

import Clientes
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R
import com.cinergia.appevolve.Ticket.CancelarPagoActivity
import com.cinergia.appevolve.main.MainActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class TicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
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

    findViewById<Button>(R.id.btnTerminar).setOnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("EXTRA_USUARIO", usuario)
        startActivity(intent)
    }

    findViewById<Button>(R.id.btnCancelarp).setOnClickListener {
        val dialogView = layoutInflater.inflate(R.layout.item_dialog2, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("✅ Cancelación Exitosa")
                .setMessage("Por favor, envía el ticket de cancelar por correo.")
                .setPositiveButton("OK") { _, _ ->
                    goToTiket(cliente, fecha, usuario, credito, observaciones, pago)
                    goToEmail(cliente, fecha, usuario, credito, observaciones, pago)
                    goToFirebase(cliente, fecha, usuario, credito, observaciones, pago)
                    dialog.dismiss()
                }
                .show()
        }

        dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    }
    private fun goToEmail(
        cliente: Clientes,
        fechaActual: String,
        usuario: String,
        credito: String,
        observaciones: String,
        pago: Int
    ) {
        val destinatario = "javier_yepez@outlook.com"

        val asunto = "Pago cancelado - ${cliente.nombre}, registrado por $usuario"
        val mensaje = """
            Nombre: ${cliente.nombre ?: "Desconocido"}
            Teléfono: ${cliente.telefono?.toString() ?: "Desconocido"}
            Dirección: ${cliente.direccion ?: "Desconocido"}
            Plan: ${cliente.plan?.toString() ?: "Desconocido"}
            Fecha del pago: $fechaActual
            No. de Contrato: ${cliente.no_contrato ?: "Desconocido"}
            Fecha de Corte: ${cliente.f_corte ?: "Desconocido"}
            Correo: ${cliente.correo ?: "Desconocido"}
            El pago fue de: $$pago
            Crédito: $credito
            Observaciones: $observaciones
            El Encargado es: $usuario
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No se encontró una app de correo", Toast.LENGTH_SHORT).show()
        }

    }

    private fun goToTiket(
        cliente: Clientes,
        fechaActual: String,
        usuario: String,
        credito: String,
        observaciones: String,
        pago: Int
    ) {
        val intent = Intent(this, CancelarPagoActivity::class.java)
        intent.putExtra("cliente", cliente)
        intent.putExtra("EXTRA_USUARIO", usuario)
        intent.putExtra("fecha", fechaActual)
        intent.putExtra("credito", credito)
        intent.putExtra("observaciones", observaciones)
        intent.putExtra("pago", pago)
        startActivity(intent)
    }

    private fun goToFirebase(
        cliente: Clientes,
        fechaActual: String,
        usuario: String,
        credito: String,
        observaciones: String,
        pago: Int
    ) {
        val database =
            FirebaseDatabase.getInstance("https://crud-jgarrix99-default-rtdb.firebaseio.com/") //Cambiar base
        val referencia = database.reference.child("pagos")

        val idPago = UUID.randomUUID().toString()

        val nombre = cliente.nombre ?: "Desconocido"
        val telefono = cliente.telefono?.toString() ?: "Desconocido"
        val direccion = cliente.direccion ?: "Desconocido"
        val plan = cliente.plan?.toString() ?: "Desconocido"
        val no_contrato = cliente.no_contrato ?: "Desconocido"
        val f_corte = cliente.f_corte ?: "Desconocido"
        val correo = cliente.correo ?: "Desconocido"
        val status = "Cancelado"

        Log.d(
            "PagoActivity",
            "Guardando pago: $nombre, $telefono, $direccion, $plan, $no_contrato, $f_corte, $correo, Fecha: $fechaActual"
        )

        val datosPago = mapOf(
            "nombre" to nombre,
            "telefono" to telefono,
            "direccion" to direccion,
            "plan" to plan,
            "fecha" to fechaActual,
            "no_contrato" to no_contrato,
            "f_corte" to f_corte,
            "correo" to correo,
            "usuario" to usuario,
            "credito" to credito,
            "descripciones" to observaciones,
            "pago" to pago,
            "status" to status
        )

        referencia.child(idPago).setValue(datosPago)
            .addOnSuccessListener {
                Toast.makeText(this, "Cancelación guardada en Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("PagoActivity", "Error al guardar la cancelación: ${e.message}")
                Toast.makeText(this, "Error al guardar la cancelación", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this)
                    .setTitle("❌ Error")
                    .setMessage("Hubo un problema al procesar la cancelación. Por favor, intenta nuevamente.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

    }
}

