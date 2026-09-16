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
    val lng: Double?,
    // Datos del perfil del conductor
    val celular: String? = null,
    val contactoEmergencia: String? = null,
    val dni: String? = null,
    val licencia: String? = null,
    val placa: String? = null,
    val zona: String? = null,
    val estado: String? = null   // estado de recojo (para el estudiante)
)
