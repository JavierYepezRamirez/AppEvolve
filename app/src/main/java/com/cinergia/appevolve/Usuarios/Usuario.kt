import com.google.firebase.database.PropertyName
import java.io.Serializable

data class Clientes(
    val correo: String? = null,
    val direccion: String? = null,
    val no_contrato: String? = null,
    val f_corte: String? = null,
    val nombre: String? = null,
    val plan: Any? = null,
    val telefono: Any? = null
) : Serializable {

    fun obtenerNombre(): String {
        return nombre?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
    }

    fun obtenerDireccion(): String {
        return direccion?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
    }

    fun obtenerCorreo(): String {
        return correo?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
    }

    // Renombramos getTelefono a obtenerTelefono
    fun obtenerTelefono(): String {
        return when (telefono) {
            is String -> telefono.ifBlank { "Desconocido" }
            is Number -> telefono.toString()
            else -> "Desconocido"
        }
    }

    fun obtenerPlan(): String {
        return when (plan) {
            is String -> plan.trim().ifBlank { "Desconocido" }
            is Number -> plan.toString()
            else -> "Desconocido"
        }
    }
}
