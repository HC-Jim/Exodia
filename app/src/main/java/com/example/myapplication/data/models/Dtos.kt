package com.example.myapplication.data.models

/**

 */

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
