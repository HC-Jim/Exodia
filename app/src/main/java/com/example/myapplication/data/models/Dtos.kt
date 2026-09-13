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
    val estado: String?
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
    @SerializedName("contacto_rol") val contactoRol: String?
)

/** Ubicación del bus (seguimiento en el mapa). */
data class UbicacionDto(
    val movilidad: String? = null,
    val lat: Double,
    val lng: Double,
    @SerializedName("actualizado_en") val actualizadoEn: String? = null
)
