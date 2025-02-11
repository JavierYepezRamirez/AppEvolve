package com.cinergia.appevolve.pago

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cinergia.appevolve.R

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
                finish() // Cierra la actividad
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                dialog.dismiss() // Cierra solo la alerta
            }

            // Mostrar el diálogo
            dialog.show()
        }

    }
}