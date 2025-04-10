package com.cinergia.appevolve.Usuarios

import Clientes
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cinergia.appevolve.R
import com.cinergia.appevolve.pago.PagoActivity
import com.google.firebase.database.*
import java.text.Normalizer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class UsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var listaUsuarios: MutableList<Clientes>
    private val usuariosConPagoEsteMes = mutableSetOf<String>() // clave: nombre-telefono

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        val usuario: String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()

        recyclerView = findViewById(R.id.recyclerView)
        val searchView: SearchView = findViewById(R.id.searchView)
        listaUsuarios = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UsuarioAdapter(listaUsuarios, usuariosConPagoEsteMes) { cliente ->
            Log.d("UsuariosActivity", "Usuario seleccionado: $cliente")
            val intent = Intent(this, PagoActivity::class.java)
            intent.putExtra("cliente", cliente)
            intent.putExtra("EXTRA_USUARIO", usuario)
            startActivity(intent)
            finish()
        }
        recyclerView.adapter = adapter

        // Primero obtener pagos
        obtenerPagos {
            // Luego obtener usuarios
            obtenerUsuarios()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText)
                return true
            }
        })
    }

    private fun obtenerPagos(callback: () -> Unit) {
        val pagosRef = FirebaseDatabase.getInstance("https://pagos-4c4bb-default-rtdb.firebaseio.com/")
            .reference.child("pagos")

        pagosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mesActual = LocalDate.now().monthValue
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                for (pagoSnapshot in snapshot.children) {
                    val nombre = pagoSnapshot.child("nombre").getValue(String::class.java) ?: continue
                    val telefono = pagoSnapshot.child("telefono").getValue(String::class.java) ?: continue
                    val fecha = pagoSnapshot.child("fecha").getValue(String::class.java) ?: continue

                    try {
                        val fechaPago = LocalDate.parse(fecha, formatter)
                        if (fechaPago.monthValue == mesActual) {
                            val clave = "${normalizeText(nombre)}-${normalizeText(telefono)}"
                            usuariosConPagoEsteMes.add(clave)
                        }
                    } catch (e: Exception) {
                        Log.e("UsuariosActivity", "Error parseando fecha: $fecha")
                    }
                }
                callback()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UsuariosActivity", "Error al leer pagos: ${error.message}")
                callback()
            }
        })
    }

    private fun obtenerUsuarios() {
        val usuariosRef = FirebaseDatabase.getInstance("https://usuarios-1c993-default-rtdb.firebaseio.com/")
            .reference.child("contactos_clientes").child("contactos_clientes")

        usuariosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaUsuarios.clear()

                val usuariosList = snapshot.children.mapNotNull { it.getValue(Clientes::class.java) }

                listaUsuarios.addAll(
                    usuariosList.filter { it.nombre != "desconocido" && it.telefono != "desconocido" }
                )

                adapter.updateList(listaUsuarios)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UsuariosActivity", "Error al leer los usuarios: ${error.message}")
            }
        })
    }

    private fun filterUsers(query: String?) {
        val normalizedQuery = normalizeText(query ?: "")

        if (normalizedQuery.isBlank()) {
            adapter.updateList(listaUsuarios) // Mostrar todos los usuarios si no hay texto
            return
        }

        val filteredList = listaUsuarios.filter {
            normalizeText(it.nombre ?: "").contains(normalizedQuery, ignoreCase = true) ||
                    normalizeText(it.telefono?.toString() ?: "").contains(normalizedQuery, ignoreCase = true)
        }

        adapter.updateList(filteredList)
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
            .lowercase(Locale.getDefault())
    }
}
