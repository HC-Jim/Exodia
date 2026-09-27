package com.example.myapplication.data.models

/**
 * Cuerpos de petición (lo que la app ENVÍA a la API). No hay conversiones ni
 * @SerializedName: el backend usa los mismos nombres (camelCase) que la app.
 *
 * Las RESPUESTAS del servidor se consumen directo en las entidades de `domain`
 * (Usuario, Alumno, Comunicado, Nota, Ubicacion, CalendarioAsistencia).
 */

/**
 * Datos que la app envía al registrar una cuenta. Lleva contraseña y pregunta/respuesta
 * de seguridad, que la entidad Usuario (de dominio) no tiene.
 */
data class UsuarioDto(
    val id: Long = 0,
    val nombre: String? = null,
    val correo: String? = null,
    val contrasena: String? = null,
    val pregunta: String? = null,
    val respuesta: String? = null,
    val rol: String? = null,
    val estudianteNombre: String? = null,
    val estudianteGrado: String? = null,
    val movilidad: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    // Datos del perfil del conductor
    val celular: String? = null,
    val contactoEmergencia: String? = null,
    val dni: String? = null,
    val licencia: String? = null,
    val placa: String? = null,
    val zona: String? = null,
    val estado: String? = null   // estado de recojo del estudiante
)

/** Cuerpo del login. */
data class LoginBody(val correo: String, val contrasena: String)

/** Cuerpo para cambiar el estado de recojo de un estudiante. */
data class EstadoBody(val estado: String)

/** Cuerpo para restablecer la contraseña. */
data class RestablecerBody(
    val correo: String,
    val respuesta: String,
    val nuevaContrasena: String
)

/** Respuesta con la pregunta de seguridad. */
data class PreguntaRespuesta(val pregunta: String? = null)

/** Respuesta simple con mensaje. */
data class MensajeRespuesta(val mensaje: String? = null)

/** Respuesta del endpoint de reiniciar ruta (cuántos estudiantes se reiniciaron). */
data class ReinicioRespuesta(val actualizados: Int = 0)
