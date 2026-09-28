package com.example.myapplication.domain


data class Recordatorio(
    val id: Long = 0,        // 0 = aún no guardado; SQLite asigna el id
    val titulo: String,
    val detalle: String,
    val fecha: String        // fecha/hora escrita por el usuario (texto libre)
)
