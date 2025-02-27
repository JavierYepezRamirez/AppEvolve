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

class UsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var listaUsuarios: MutableList<Clientes>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        val usuario: String = intent.extras?.getString("EXTRA_USUARIO").orEmpty()

        recyclerView = findViewById(R.id.recyclerView)
        val searchView: SearchView = findViewById(R.id.searchView)
        listaUsuarios = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UsuarioAdapter(listaUsuarios) {cliente ->
            Log.d("UsuariosActivity", "Usuario seleccionado: $cliente")
             val intent = Intent(this, PagoActivity::class.java)
             intent.putExtra("cliente", cliente)
             intent.putExtra("EXTRA_USUARIO", usuario)
             startActivity(intent)
            finish()
        }
        recyclerView.adapter = adapter

        // Obtener usuarios desde Firebase
        val database = FirebaseDatabase.getInstance("https://usuarios-1c993-default-rtdb.firebaseio.com/")
            .reference.child("contactos_clientes").child("contactos_clientes")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("UsuariosActivity", "Datos de Firebase: ${snapshot.value}")

                listaUsuarios.clear()

                val usuariosList = snapshot.children.mapNotNull { it.getValue(Clientes::class.java) }

                listaUsuarios.addAll(
                    usuariosList.filter { it.nombre != "desconocido" && it.telefono != "desconocido" }
                )

                if (listaUsuarios.isEmpty()) {
                    Log.d("UsuariosActivity", "No hay usuarios disponibles")
                } else {
                    Log.d("UsuariosActivity", "Usuarios cargados correctamente: ${listaUsuarios.size}")
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UsuariosActivity", "Error al leer los usuarios: ${error.message}")
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText)
                return true
            }
        })
    }

    private fun filterUsers(query: String?) {
        val normalizedQuery = normalizeText(query ?: "")

        val filteredList = listaUsuarios.filter {
            normalizeText(it.nombre ?: "").contains(normalizedQuery, ignoreCase = true) ||
                    normalizeText(it.telefono?.toString() ?: "").contains(normalizedQuery, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
    }
}
