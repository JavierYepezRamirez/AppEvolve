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

        val correo = cliente.correo ?: "Desconocido"
        val direccion = cliente.direccion ?: "Desconocido"
        val no_contrato = cliente.no_contrato ?: "Desconocido"
        val f_corte = cliente.f_corte ?: "Desconocido"
        val nombre = cliente.nombre ?: "Desconocido"
        val plan = cliente.plan?.toString() ?: "Desconocido"
        val telefono = cliente.telefono?.toString() ?: "Desconocido"

        val fechaActual: String = LocalDate.now().toString()

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvTelefono = findViewById<TextView>(R.id.tvTelefono)
        val tvDireccion = findViewById<TextView>(R.id.tvDireccion)
        val tvPlan = findViewById<TextView>(R.id.tvPan)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)

        tvNombre.text = "Nombre: $nombre"
        tvTelefono.text = "Telefono: $telefono"
        tvDireccion.text = "Direccion: $direccion"
        tvPlan.text = "Plan($): $plan"
        tvFecha.text = "Fecha: $fechaActual"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAceptar = findViewById<AppCompatButton>(R.id.btnAceptar)
        val btnCancelar = findViewById<AppCompatButton>(R.id.btnCancelar)

        btnCancelar.setOnClickListener {
            // Inflar el diseño personalizado del diálogo
            val dialogView = layoutInflater.inflate(R.layout.item_dialog, null)

            // Crear el AlertDialog
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Configurar acciones de los botones
            dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
                val intent = Intent(this, UsuariosActivity::class.java)
                intent.putExtra("EXTRA_USUARIO", usuario)
                startActivity(intent)
                finish() // Cierra la actividad
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                dialog.dismiss() // Cierra solo la alerta
            }
            // Mostrar el diálogo
            dialog.show()
        }

        btnAceptar.setOnClickListener{
            val dialogView = layoutInflater.inflate(R.layout.item_pago, null)

            val cb250 = dialogView.findViewById<CheckBox>(R.id.cb250)
            val cb350 = dialogView.findViewById<CheckBox>(R.id.cb350)
            val cb450 = dialogView.findViewById<CheckBox>(R.id.cb450)
            val etCredito = dialogView.findViewById<EditText>(R.id.etCredito)
            val etObservaciones = dialogView.findViewById<EditText>(R.id.etObservaciones)
            var pago = 0

            if (cb250.isChecked) {
                pago = 250
            }
            if (cb350.isChecked) {
                pago = 350
            }
            if (cb450.isChecked) {
                pago = 450
            }

            val credito = if (etCredito.text.toString().isBlank()) "Ninguno" else etCredito.text.toString()
            val observaciones = if (etObservaciones.text.toString().isBlank()) "Ninguno" else etObservaciones.text.toString()

            if (etCredito.text.toString().isBlank()) etCredito.setText("Ninguno")
            if (etObservaciones.text.toString().isBlank()) etObservaciones.setText("Ninguno")

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnAceptar).setOnClickListener {
                // Mostrar el diálogo de confirmación
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Pago Exitoso")
                    .setMessage("Su pago fue correctamente capturado")
                    .setPositiveButton("OK") { dialog, _ ->
                        goToTiket(cliente, fechaActual, usuario, credito, observaciones, pago)
                        goToEmail(cliente, fechaActual, usuario, credito, observaciones, pago)
                        goToFirebase(cliente, fechaActual, usuario, credito, observaciones, pago)

                        finish()
                    }
                    .show()
            }

            dialogView.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()}

        }

    private fun goToEmail(cliente: Clientes, fechaActual: String, usuario: String, credito: String, observaciones: String, pago: Int) {
        val destinatario = "javier_yepez@outlook.com"  //Cambiar por correo de Felipe
        val asunto = "Pago registrado - ${cliente.nombre}, registrado por $usuario"
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
        Credito: $credito
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

    private fun goToTiket(cliente: Clientes, fechaActual: String, usuario: String, credito: String, observaciones: String, pago: Int) {
        //val intent = Intent(this, OtraActivity::class.java) Tiket
        intent.putExtra("cliente", cliente)
        intent.putExtra("EXTRA_USUARIO", usuario)
        intent.putExtra("fecha", fechaActual)
        intent.putExtra("credito", credito)
        intent.putExtra("observaciones", observaciones)
        intent.putExtra("pago", pago)
    }

    private fun goToFirebase(cliente: Clientes, fechaActual: String, usuario: String, credito: String, observaciones: String, pago: Int) {
        val database = FirebaseDatabase.getInstance("https://crud-jgarrix99-default-rtdb.firebaseio.com/") //Cambiar base
        val referencia = database.reference.child("pagos")

        val idPago = UUID.randomUUID().toString()

        val nombre = cliente.nombre ?: "Desconocido"
        val telefono = cliente.telefono?.toString() ?: "Desconocido"
        val direccion = cliente.direccion ?: "Desconocido"
        val plan = cliente.plan?.toString() ?: "Desconocido"
        val no_contrato = cliente.no_contrato ?: "Desconocido"
        val f_corte = cliente.f_corte ?: "Desconocido"
        val correo = cliente.correo ?: "Desconocido"

        Log.d("PagoActivity", "Guardando pago: $nombre, $telefono, $direccion, $plan, $no_contrato, $f_corte, $correo, Fecha: $fechaActual")

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
            "pago" to pago
        )

        referencia.child(idPago).setValue(datosPago)
            .addOnSuccessListener {
                Toast.makeText(this, "Pago guardado en Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Mostrar el error que ocurrió al guardar
                Log.e("PagoActivity", "Error al guardar el pago: ${e.message}")
                Toast.makeText(this, "Error al guardar el pago", Toast.LENGTH_SHORT).show()
            }

    }
}