import com.google.firebase.database.PropertyName
import java.io.Serializable

data class Clientes(
    val id: String? = null,
    val coordenadas: String? = null,
    val nombre: String? = null,
    val plan: Any? = null,
    val telefono: Any? = null,
    val comunidad: String? = null,
) : Serializable {

    fun obtenerid(): String {
        return id?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
    }

    fun obtenerNombre(): String {
        return nombre?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
    }

    fun obtenerDireccion(): String {
        return coordenadas?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
    }

    fun obtenerCorreo(): String {
        return comunidad?.trim()?.ifBlank { "Desconocido" } ?: "Desconocido"
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
