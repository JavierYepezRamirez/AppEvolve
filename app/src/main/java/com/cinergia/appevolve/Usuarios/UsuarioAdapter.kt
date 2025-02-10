package com.cinergia.appevolve.Usuarios

import Clientes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cinergia.appevolve.R

class UsuarioAdapter(
    private var listaUsuarios: List<Clientes>,
    private val listener: (Clientes) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        // Manejo seguro de valores nulos y vacíos
        holder.tvNombre.text = usuario.nombre?.ifBlank { "Desconocido" } ?: "Desconocido"
        holder.tvTelefono.text = usuario.obtenerTelefono()

        // Configurar evento de clic en el item
        holder.itemView.setOnClickListener { listener(usuario) }
    }

    override fun getItemCount(): Int = listaUsuarios.size

    fun updateList(newList: List<Clientes>) {
        listaUsuarios = newList
        notifyDataSetChanged() // Notifica al adaptador que se ha actualizado la lista
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
    }
}
