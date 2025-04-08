package com.cinergia.appevolve.Usuarios

import Clientes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cinergia.appevolve.R
import java.text.Normalizer
import java.util.*

class UsuarioAdapter(
    private var listaUsuarios: List<Clientes>,
    private val usuariosConPagoEsteMes: Set<String>,
    private val listener: (Clientes) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        val nombre = usuario.nombre?.ifBlank { "Desconocido" } ?: "Desconocido"
        val telefono = usuario.obtenerTelefono()
        val clave = "${normalizeText(nombre)}-${normalizeText(telefono)}"
        val context = holder.itemView.context

        val estado = if (usuariosConPagoEsteMes.contains(clave)) "✅" else "⌛"
        holder.tvNombre.text = "$nombre $estado"
        holder.tvTelefono.text = telefono

        if (usuariosConPagoEsteMes.contains(clave)) {
            val verdeClaro = ContextCompat.getColor(context, R.color.verde_claro)
            holder.cardView.setCardBackgroundColor(verdeClaro)
        } else {
            val defaultColor = ContextCompat.getColor(context, R.color.item_background)
            holder.cardView.setCardBackgroundColor(defaultColor)
        }

        holder.itemView.setOnClickListener { listener(usuario) }
    }


    override fun getItemCount(): Int = listaUsuarios.size

    fun updateList(newList: List<Clientes>) {
        listaUsuarios = newList
        notifyDataSetChanged()
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val cardView: CardView = itemView as CardView
    }

    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(Regex("[\\p{InCombiningDiacriticalMarks}]"), "")
            .lowercase(Locale.getDefault())
    }
}
