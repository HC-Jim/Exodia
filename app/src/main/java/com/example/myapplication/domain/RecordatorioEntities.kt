package com.example.myapplication.domain

/**
 * Recordatorio / nota personal del estudiante.
 * Es un dato 100% local (SQLite): lo crea el usuario y vive solo en su teléfono.
 */
data class Recordatorio(
    val id: Long = 0,        // 0 = aún no guardado; SQLite asigna el id
    val titulo: String,
    val detalle: String,
    val fecha: String        // fecha/hora escrita por el usuario (texto libre)
)
