package com.cinergia.appevolve.pago

import Clientes
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R
import com.cinergia.appevolve.Usuarios.UsuariosActivity
import com.cinergia.appevolve.Ticket.TicketActivity
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.util.UUID

class PagoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cliente = intent.getSerializableExtra("cliente") as? Clientes ?: Clientes()
        val usuario: String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()

        val fechaActual: String = LocalDate.now().toString()

        findViewById<TextView>(R.id.tvNombre).text = "Nombre: ${cliente.nombre ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvTelefono).text = "Teléfono: ${cliente.telefono ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvPan).text = "Plan($): ${cliente.plan ?: "Desconocido"}"
        findViewById<TextView>(R.id.tvFecha).text = "Fecha: $fechaActual"


        findViewById<AppCompatButton>(R.id.btnCancelar).setOnClickListener {
            mostrarDialogoCancelar(
                usuario
            )
        }
        findViewById<AppCompatButton>(R.id.btnAceptar).setOnClickListener {
            mostrarDialogoPago(
                cliente,
                fechaActual,
                usuario
            )
        }
    }

    private fun mostrarDialogoCancelar(usuario: String) {
        val dialogView = layoutInflater.inflate(R.layout.item_dialog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
            startActivity(Intent(this, UsuariosActivity::class.java).apply {
                putExtra("EXTRA_USUARIO", usuario)
            })
            finish()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogoPago(cliente: Clientes, fechaActual: String, usuario: String) {
        val dialogView = layoutInflater.inflate(R.layout.item_pago, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val cbCredito = dialogView.findViewById<CheckBox>(R.id.cbCredito)
        val etCredito = dialogView.findViewById<EditText>(R.id.etCredito)
        val btnAceptarItem = dialogView.findViewById<Button>(R.id.btnAceptar)

        etCredito.isEnabled = false
        etCredito.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        btnAceptarItem.isEnabled = false

        val checkBoxes = listOf(
            dialogView.findViewById<CheckBox>(R.id.cb250),
            dialogView.findViewById<CheckBox>(R.id.cb350),
            dialogView.findViewById<CheckBox>(R.id.cb450),
            cbCredito
        )

        fun actualizarEstado(selectedCheckBox: CheckBox) {
            checkBoxes.forEach { checkBox ->
                if (checkBox != selectedCheckBox) {
                    checkBox.isChecked = false
                }
            }

            btnAceptarItem.isEnabled = selectedCheckBox.isChecked
            etCredito.isEnabled = cbCredito.isChecked

            if (!cbCredito.isChecked) {
                etCredito.text.clear()
            }
        }

        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    actualizarEstado(checkBox)
                }
            }
        }

        btnAceptarItem.setOnClickListener {
            if (checkBoxes.none { it.isChecked }) {
                Toast.makeText(this, "Seleccione un monto antes de continuar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cbCredito.isChecked && etCredito.text.isNullOrEmpty()) {
                Toast.makeText(this, "Por favor, ingresa el monto del crédito.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pago = when {
                checkBoxes[0].isChecked -> 250
                checkBoxes[1].isChecked -> 350
                checkBoxes[2].isChecked -> 450
                cbCredito.isChecked -> etCredito.text.toString().toIntOrNull() ?: 0
                else -> 0
            }

            val observaciones =
                dialogView.findViewById<EditText>(R.id.etObservaciones).text.toString()
                    .ifBlank { "Ninguno" }

            AlertDialog.Builder(this)
                .setTitle("✅ Pago Exitoso")
                .setMessage("Por favor, envía el ticket de pago por correo.")
                .setPositiveButton("OK") { _, _ ->
                    goToTiket(cliente, fechaActual, usuario, observaciones, pago)
                    goToEmail(cliente, fechaActual, usuario, observaciones, pago)
                    goToFirebase(cliente, fechaActual, usuario, observaciones, pago)
                    finish()
                }
                .show()
        }

        dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

        val asunto = "Pago registrado - ${cliente.nombre}, registrado por $usuario"
        val mensaje = """
            ID: ${cliente.id ?: "Desconocido"}
            Nombre: ${cliente.nombre ?: "Desconocido"}
            Teléfono: ${cliente.telefono?.toString() ?: "Desconocido"}
            Coordenadas: ${cliente.coordenadas ?: "Desconocido"}
            Comunidad: ${cliente.comunidad ?: "Desconocido"}
            Plan: ${cliente.plan?.toString() ?: "Desconocido"}
            Fecha del pago: $fechaActual
            El pago fue de: $$pago
            Tipo de pago: $tipoPago
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
        observaciones: String,
        pago: Int
    ) {
        val intent = Intent(this, TicketActivity::class.java)
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
        val status = "Completado"

        var tipoPago = ""

        if(usuario == "EvolveAdmin") {
            tipoPago = "Transferencia"
        } else {
            tipoPago = "Efectivo"
        }

        Log.d(
            "PagoActivity",
            "Guardando pago: $nombre, $telefono, $coordenadas, $comunidad, $plan, Fecha: $fechaActual, $id"
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
            "status" to status,
            "pago" to pago,
            "tipoPago" to tipoPago
        )

        referencia.child(idPago).setValue(datosPago)
            .addOnSuccessListener {
                Toast.makeText(this, "Pago guardado en Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("PagoActivity", "Error al guardar el pago: ${e.message}")
                Toast.makeText(this, "Error al guardar el pago", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(this)
                    .setTitle("❌ Error")
                    .setMessage("Hubo un problema al procesar el pago. Por favor, intenta nuevamente.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

    }
}