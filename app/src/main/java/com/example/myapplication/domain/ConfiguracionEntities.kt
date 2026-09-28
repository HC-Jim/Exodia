package com.example.myapplication.domain

/**
 * Entidades del área de Configuración (persistencia local).
 */


data class ContactoEmergencia(
    val id: Long = 0,        // 0 = aún no guardado; SQLite asigna el id real
    val nombre: String,
    val telefono: String
)
