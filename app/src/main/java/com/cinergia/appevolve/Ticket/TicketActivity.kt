package com.cinergia.appevolve.Ticket

import Clientes
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R
import com.cinergia.appevolve.main.MainActivity
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID
import kotlin.math.log

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
        val observaciones:String = intent.extras?.getString("observaciones").orEmpty()
        val pago: Int = intent.extras?.getInt("pago", 0) ?: 0

        findViewById<TextView>(R.id.tvNombre).text = "Nombre: ${cliente.nombre ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvTelefono).text = "Teléfono: ${cliente.telefono ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvPlan).text = "Plan($): ${cliente.plan ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvFecha).text = "Fecha: $fecha"


        findViewById<Button>(R.id.btnTerminar).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("EXTRA_USUARIO", usuario)
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.btnCancelart).setOnClickListener {
            Log.d(
                "Se toco el boton",
                "Holaaaaa"
            )
            val dialogView = layoutInflater.inflate(R.layout.item_dialog2, null)
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()

            dialog.show()

            dialogView.findViewById<Button>(R.id.btnYes2).setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("✅ Cancelación Exitosa")
                    .setMessage("Por favor, envía el ticket de pago por correo.")
                    .setPositiveButton("OK") { _, _ ->
                    }
                        goToTiket(cliente, fecha, usuario, observaciones, pago)
                        goToEmail(cliente, fecha, usuario, observaciones, pago)
                        goToFirebase(cliente, fecha, usuario, observaciones, pago)
                    dialog.show()
            }
            dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                dialog.dismiss()
            }
        }
    }
    private fun goToEmail(
        cliente: Clientes,
        fechaActual: String,
        usuario: String,
        observaciones: String,
        pago: Int
    ) {
        var tipoPago = ""

        if(usuario == "EvolveAdmin") {
            tipoPago = "Transferencia"
        } else {
            tipoPago = "Efectivo"
        }
        val destinatario = "pagos.evolvemx@gmail.com"

        val asunto = "Pago cancelado - ${cliente.nombre}, registrado por $usuario"
        val mensaje = """
            ID: ${cliente.id ?: "id"}
            Nombre: ${cliente.nombre ?: "Desconocido"}
            Teléfono: ${cliente.telefono?.toString() ?: "Desconocido"}
            Coordenadas: ${cliente.coordenadas ?: "Desconocido"}
            Comunidad: ${cliente.comunidad ?: "Desconocido"}
            Plan: ${cliente.plan?.toString() ?: "Desconocido"}
            Fecha del pago: $fechaActual
            El pago fue de: $$pago
            Observaciones: $observaciones
            Tipo de pago: $tipoPago
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
        observaciones: String,
        pago: Int
    ) {
        val intent = Intent(this, CancelarPagoActivity::class.java)
        intent.putExtra("cliente", cliente)
        intent.putExtra("EXTRA_USUARIO", usuario)
        intent.putExtra("fecha", fechaActual)
        intent.putExtra("observaciones", observaciones)
        intent.putExtra("pago", pago)
        startActivity(intent)
    }

    private fun goToFirebase(
        cliente: Clientes,
        fechaActual: String,
        usuario: String,
        observaciones: String,
        pago: Int
    ) {
        val database =
            FirebaseDatabase.getInstance("https://pagos-4c4bb-default-rtdb.firebaseio.com/") //Cambiar base
        val referencia = database.reference.child("pagos")

        val idPago = UUID.randomUUID().toString()

        val id = cliente.id ?: "Desconocido"
        val nombre = cliente.nombre ?: "Desconocido"
        val telefono = cliente.telefono?.toString() ?: "Desconocido"
        val coordenadas = cliente.coordenadas ?: "Desconocido"
        val comunidad = cliente.comunidad ?: "Desconocido"
        val plan = cliente.plan?.toString() ?: "Desconocido"
        val status = "Cancelado"

        var tipoPago = ""

        if(usuario == "EvolveAdmin") {
            tipoPago = "Transferencia"
        } else {
            tipoPago = "Efectivo"
        }

        Log.d(
            "PagoActivity",
            "Guardando pago: $nombre, $telefono, $coordenadas. $comunidad, $plan, Fecha: $fechaActual, $id"
        )

        val datosPago = mapOf(
            "id" to id,
            "nombre" to nombre,
            "telefono" to telefono,
            "coordenadas" to coordenadas,
            "comunidad" to comunidad,
            "plan" to plan,
            "fecha" to fechaActual,
            "usuario" to usuario,
            "descripciones" to observaciones,
            "pago" to pago,
            "status" to status,
            "tipoPago" to tipoPago
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

