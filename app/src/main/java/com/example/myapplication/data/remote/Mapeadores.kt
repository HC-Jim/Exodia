package com.example.myapplication.data.remote

import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.models.AlumnoDto
import com.example.myapplication.data.models.ComunicadoDto
import com.example.myapplication.data.models.HijoDto
import com.example.myapplication.data.models.NotaDto
import com.example.myapplication.data.models.UbicacionDto
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.Comunicado
import com.example.myapplication.domain.entities.EstadoEntrega
import com.example.myapplication.domain.entities.Hijo
import com.example.myapplication.domain.entities.Nota
import com.example.myapplication.domain.entities.Ubicacion
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.AccentPink
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.WarningAmber

/**
 * Convierte los DTOs (JSON de la API) a las entidades del dominio que usan las pantallas.
 * El JSON no trae "color", así que aquí le asignamos uno según su posición en la lista.
 */

// Paleta que se repite para dar color a las tarjetas.
private val paleta = listOf(IndigoPrimary, AccentBlue, AccentPink, SuccessGreen, WarningAmber)
private fun colorPorIndice(indice: Int): Color = paleta[indice % paleta.size]

// ---------- ALUMNO ----------
fun AlumnoDto.aDominio(): Alumno = Alumno(
    id = id.toString(),
    nombre = nombre,
    grado = grado ?: "",
    direccion = direccion ?: "",
    paradero = paradero ?: "",
    horaEntrega = horaEntrega,
    estado = when (estado) {
        "ABORDO" -> EstadoEntrega.ABORDO
        "ENTREGADO" -> EstadoEntrega.ENTREGADO
        else -> EstadoEntrega.PENDIENTE
    }
)

// ---------- COMUNICADO ----------
fun List<ComunicadoDto>.aComunicados(): List<Comunicado> = mapIndexed { i, dto ->
    Comunicado(
        id = dto.id.toString(),
        titulo = dto.titulo,
        detalle = dto.detalle ?: "",
        fecha = dto.fecha ?: "",
        color = colorPorIndice(i)
    )
}

// ---------- NOTA ----------
fun List<NotaDto>.aNotas(): List<Nota> = mapIndexed { i, dto ->
    Nota(
        id = dto.id.toString(),
        curso = dto.curso,
        detalle = dto.detalle ?: "",
        valor = dto.valor ?: "",
        color = colorPorIndice(i)
    )
}

// ---------- UBICACIÓN ----------
fun UbicacionDto.aDominio(): Ubicacion = Ubicacion(
    movilidad = movilidad ?: "",
    lat = lat,
    lng = lng,
    actualizado = actualizadoEn ?: ""
)

// ---------- HIJO ----------
fun List<HijoDto>.aHijos(): List<Hijo> = mapIndexed { i, dto ->
    Hijo(
        id = dto.id.toString(),
        nombre = dto.nombre,
        grado = dto.grado ?: "",
        movilidad = dto.movilidad ?: "",
        paradero = dto.paradero ?: "",
        contactoNombre = dto.contactoNombre ?: "",
        contactoRol = dto.contactoRol ?: "",
        color = colorPorIndice(i)
    )
}
