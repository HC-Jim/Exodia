package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

/**
 * DTOs (Data Transfer Objects): representan EXACTAMENTE el JSON que devuelve la API.
 * Gson usa @SerializedName para emparejar los nombres del JSON (snake_case)
 * con las propiedades de Kotlin (camelCase).
 *
 * Estos DTOs luego se convierten a las entidades del dominio en `data/remote/Mapeadores.kt`.
 */

data class AlumnoDto(
    val id: Long,
    val nombre: String,
    val grado: String?,
    val direccion: String?,
    val paradero: String?,
    @SerializedName("hora_entrega") val horaEntrega: String?,
    val estado: String?,
    val lat: Double? = null,
    val lng: Double? = null
)

data class ComunicadoDto(
    val id: Long,
    val titulo: String,
    val detalle: String?,
    val fecha: String?
)

data class NotaDto(
    val id: Long,
    val curso: String,
    val detalle: String?,
    val valor: String?
)

data class HijoDto(
    val id: Long,
    val nombre: String,
    val grado: String?,
    val movilidad: String?,
    val paradero: String?,
    @SerializedName("contacto_nombre") val contactoNombre: String?,
    @SerializedName("contacto_rol") val contactoRol: String?,
    val lat: Double? = null,
    val lng: Double? = null
)

/** Ubicación del bus (seguimiento en el mapa). */
data class UbicacionDto(
    val movilidad: String? = null,
    val lat: Double,
    val lng: Double,
    @SerializedName("actualizado_en") val actualizadoEn: String? = null
)

// ---------- Autenticación ----------

/** Usuario tal como lo envía/recibe la API. */
data class UsuarioDto(
    val id: Long = 0,
    val nombre: String? = null,
    val correo: String? = null,
    val contrasena: String? = null,
    val pregunta: String? = null,
    val respuesta: String? = null,
    val rol: String? = null,
    @SerializedName("estudiante_nombre") val estudianteNombre: String? = null,
    @SerializedName("estudiante_grado") val estudianteGrado: String? = null,
    val movilidad: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

/** Cuerpo del login. */
data class LoginBody(val correo: String, val contrasena: String)

/** Cuerpo para restablecer la contraseña. */
data class RestablecerBody(
    val correo: String,
    val respuesta: String,
    @SerializedName("nueva_contrasena") val nuevaContrasena: String
)

/** Respuesta con la pregunta de seguridad. */
data class PreguntaRespuesta(val pregunta: String? = null)

/** Respuesta simple con mensaje. */
data class MensajeRespuesta(val mensaje: String? = null)
