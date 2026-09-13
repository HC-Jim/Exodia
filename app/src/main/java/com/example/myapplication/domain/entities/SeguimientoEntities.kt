package com.example.myapplication.domain.entities

/**
 * Entidades del seguimiento del transporte (mapa en vivo).
 */

/** Posición actual del bus de una movilidad. */
data class Ubicacion(
    val movilidad: String,
    val lat: Double,
    val lng: Double,
    val actualizado: String
)
