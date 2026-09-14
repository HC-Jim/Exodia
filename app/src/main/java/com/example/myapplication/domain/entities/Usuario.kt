package com.example.myapplication.domain.entities

/**
 * Usuario autenticado. Cada usuario tiene UN solo estudiante asociado
 * (los campos estudiante* aplican al rol Apoderado).
 */
data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String,                    // ESTUDIANTE | CONDUCTOR
    val estudianteNombre: String?,
    val estudianteGrado: String?,
    val movilidad: String?,
    val lat: Double?,
    val lng: Double?
)
