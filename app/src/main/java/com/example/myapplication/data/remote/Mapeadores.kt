package com.example.myapplication.data.remote

import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.models.ComunicadoDto
import com.example.myapplication.data.models.NotaDto
import com.example.myapplication.data.models.UbicacionDto
import com.example.myapplication.data.models.UsuarioDto
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
 * Convierte los DTOs (JSON de la API) a las entidades del dominio que usan las pantallas.
 * El JSON no trae "color", asi que aqui le asignamos uno segun su posicion en la lista.
 */

// Colores que se van repitiendo para las tarjetas.
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
