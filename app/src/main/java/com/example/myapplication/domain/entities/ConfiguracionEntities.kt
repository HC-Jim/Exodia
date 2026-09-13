package com.example.myapplication.domain.entities

/**
 * Entidades del área de Configuración (persistencia local).
 */

/** Un contacto de emergencia guardado en la base de datos SQLite local. */
data class ContactoEmergencia(
    val id: Long = 0,        // 0 = aún no guardado; SQLite asigna el id real
    val nombre: String,
    val telefono: String
)
