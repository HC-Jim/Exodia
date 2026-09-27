package com.example.myapplication.domain

/**
 * Entidades del seguimiento del transporte (mapa en vivo).
 */

/**
 * Posición actual del bus de una movilidad.
 * Lo llena Gson directo del JSON del servidor (el backend usa los mismos nombres).
 */
data class Ubicacion(
    val movilidad: String? = null,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val actualizado: String? = null
)
