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
 * El JSON no trae "color", asi que aqui le asignamos uno segun su posicion en la lista.
 */

// Colores que se van repitiendo para las tarjetas.
private val paleta = listOf(IndigoPrimary, AccentBlue, AccentPink, SuccessGreen, WarningAmber)

private fun colorPorIndice(indice: Int): Color {
    val posicion = indice % paleta.size   // vuelve a empezar cuando se acaban los colores
    return paleta[posicion]
}

// ---------- ALUMNO ----------
fun AlumnoDto.aDominio(): Alumno {
    // Convierte el texto del estado al enum del dominio.
    val estadoEnum: EstadoEntrega
    if (estado == "ABORDO") {
        estadoEnum = EstadoEntrega.ABORDO
    } else if (estado == "ENTREGADO") {
        estadoEnum = EstadoEntrega.ENTREGADO
    } else {
        estadoEnum = EstadoEntrega.PENDIENTE
    }

    return Alumno(
        id = id.toString(),
        nombre = nombre,
        grado = grado ?: "",
        direccion = direccion ?: "",
        paradero = paradero ?: "",
        horaEntrega = horaEntrega,
        estado = estadoEnum,
        lat = lat,
        lng = lng
    )
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

// ---------- HIJO ----------
fun List<HijoDto>.aHijos(): List<Hijo> {
    val lista = mutableListOf<Hijo>()
    for (i in this.indices) {
        val dto = this[i]
        val hijo = Hijo(
            id = dto.id.toString(),
            nombre = dto.nombre,
            grado = dto.grado ?: "",
            movilidad = dto.movilidad ?: "",
            paradero = dto.paradero ?: "",
            contactoNombre = dto.contactoNombre ?: "",
            contactoRol = dto.contactoRol ?: "",
            color = colorPorIndice(i),
            lat = dto.lat,
            lng = dto.lng
        )
        lista.add(hijo)
    }
    return lista
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
