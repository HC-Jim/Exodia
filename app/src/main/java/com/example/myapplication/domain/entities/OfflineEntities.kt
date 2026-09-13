package com.example.myapplication.domain.entities

/**
 * Entidades del soporte offline (persistencia local en SQLite).
 */

/** Una entrega registrada en el historial local del conductor. */
data class RegistroHistorial(
    val id: Long,
    val alumnoNombre: String,
    val estado: String,
    val fecha: String        // "yyyy-MM-dd HH:mm"
)
