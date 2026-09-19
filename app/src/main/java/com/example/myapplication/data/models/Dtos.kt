package com.example.myapplication.data.models

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.Comunicado
import com.example.myapplication.domain.entities.EstadoAsistencia
import com.example.myapplication.domain.entities.EstadoEntrega
import com.example.myapplication.domain.entities.Nota
import com.example.myapplication.domain.entities.Ubicacion
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.AccentPink
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.WarningAmber

/**
 * DTOs (Data Transfer Objects): representan EXACTAMENTE el JSON que devuelve la API.
 * Gson usa @SerializedName para emparejar los nombres del JSON (snake_case)
 * con las propiedades de Kotlin (camelCase).
 *
 * Al final del archivo están las conversiones DTO -> entidad de dominio, para tener
 * el modelo y su traducción juntos en un solo lugar.
 */

/** Comunicado tal como llega de la API. */
data class ComunicadoDto(
    val id: Long,
    val titulo: String,
    val detalle: String?,
    val fecha: String?
)

/** Nota/calificación tal como llega de la API. */
data class NotaDto(
    val id: Long,
    val curso: String,
    val detalle: String?,
    val valor: String?
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
    val lng: Double? = null,
    // Datos del perfil del conductor
    val celular: String? = null,
    @SerializedName("contacto_emergencia") val contactoEmergencia: String? = null,
    val dni: String? = null,
    val licencia: String? = null,
    val placa: String? = null,
    val zona: String? = null,
    val estado: String? = null   // estado de recojo del estudiante
)

/** Día de asistencia registrado. */
data class AsistenciaDto(
    val id: Long = 0,
    val dia: Int = 0,
    val estado: String? = null
)

/** Cuerpo del login. */
data class LoginBody(val correo: String, val contrasena: String)

/** Cuerpo para cambiar el estado de recojo de un estudiante. */
data class EstadoBody(val estado: String)

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


// ============================================================
//  Conversiones DTO -> entidad de dominio
//  (antes vivían en data/remote/Mapeadores.kt; ahora van junto a sus DTOs)
// ============================================================

// El JSON no trae "color": aquí se le asigna uno según su posición en la lista.
private val paleta = listOf(IndigoPrimary, AccentBlue, AccentPink, SuccessGreen, WarningAmber)

private fun colorPorIndice(indice: Int): Color {
    val posicion = indice % paleta.size   // vuelve a empezar cuando se acaban los colores
    return paleta[posicion]
}

// ---------- ESTUDIANTE (usuario) -> ALUMNO ----------
fun List<UsuarioDto>.aAlumnos(): List<Alumno> {
    val lista = mutableListOf<Alumno>()
    for (dto in this) {
        val estadoEnum: EstadoEntrega
        if (dto.estado == "ENTREGADO") {
            estadoEnum = EstadoEntrega.ENTREGADO
        } else if (dto.estado == "CANCELADO") {
            estadoEnum = EstadoEntrega.CANCELADO
        } else {
            estadoEnum = EstadoEntrega.PENDIENTE
        }

        lista.add(
            Alumno(
                id = dto.id.toString(),
                nombre = dto.estudianteNombre ?: dto.nombre ?: "Estudiante",
                grado = dto.estudianteGrado ?: "",
                direccion = "",
                paradero = dto.movilidad ?: "",
                horaEntrega = null,
                estado = estadoEnum,
                lat = dto.lat,
                lng = dto.lng
            )
        )
    }
    return lista
}

// ---------- COMUNICADO ----------
fun List<ComunicadoDto>.aComunicados(): List<Comunicado> {
    val lista = mutableListOf<Comunicado>()
    for (i in this.indices) {
        val dto = this[i]
        val comunicado = Comunicado(
            id = dto.id.toString(),
            titulo = dto.titulo,
            detalle = dto.detalle ?: "",
            fecha = dto.fecha ?: "",
            color = colorPorIndice(i)
        )
        lista.add(comunicado)
    }
    return lista
}

// ---------- NOTA ----------
fun List<NotaDto>.aNotas(): List<Nota> {
    val lista = mutableListOf<Nota>()
    for (i in this.indices) {
        val dto = this[i]
        val nota = Nota(
            id = dto.id.toString(),
            curso = dto.curso,
            detalle = dto.detalle ?: "",
            valor = dto.valor ?: "",
            color = colorPorIndice(i)
        )
        lista.add(nota)
    }
    return lista
}

// ---------- ASISTENCIA ----------
fun estadoAsistenciaDe(texto: String?): EstadoAsistencia {
    return if (texto == "TARDANZA") EstadoAsistencia.TARDANZA
    else if (texto == "FALTA") EstadoAsistencia.FALTA
    else if (texto == "JUSTIFICADO") EstadoAsistencia.JUSTIFICADO
    else if (texto == "SIN_CLASE") EstadoAsistencia.SIN_CLASE
    else EstadoAsistencia.PRESENTE
}

// ---------- UBICACION ----------
fun UbicacionDto.aDominio(): Ubicacion {
    return Ubicacion(
        movilidad = movilidad ?: "",
        lat = lat,
        lng = lng,
        actualizado = actualizadoEn ?: ""
    )
}
