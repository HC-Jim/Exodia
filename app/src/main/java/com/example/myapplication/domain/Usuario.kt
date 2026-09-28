package com.example.myapplication.domain

/**
 * Usuario de la app. Cada usuario tiene UN solo estudiante asociado.
 *
 * Las credenciales (contrasena, pregunta, respuesta) se usan al enviar el registro
 * y también las devuelve el servidor (proyecto universitario: se prioriza la
 * simplicidad; en una app real no se devolverían).
 */
data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val correo: String,
    val rol: String,                    // ESTUDIANTE | CONDUCTOR
    // Credenciales (solo de ida, al registrarse)
    val contrasena: String? = null,
    val pregunta: String? = null,
    val respuesta: String? = null,
    // Datos del estudiante
    val estudianteNombre: String? = null,
    val estudianteGrado: String? = null,
    val movilidad: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    // Datos del perfil del conductor
    val celular: String? = null,
    val contactoEmergencia: String? = null,
    val dni: String? = null,
    val licencia: String? = null,
    val placa: String? = null,
    val zona: String? = null,
    val estado: String? = null   // estado de recojo (para el estudiante)
)
