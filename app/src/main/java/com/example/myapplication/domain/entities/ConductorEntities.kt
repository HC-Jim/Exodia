package com.example.myapplication.domain.entities

/**
 * Entidades de negocio del rol Conductor.
 * Objetos puros del dominio, independientes de la interfaz y de la fuente de datos.
 */

/** Estado de entrega de un alumno dentro de la ruta activa. */
enum class EstadoEntrega {
    PENDIENTE,
    ABORDO,
    ENTREGADO
}

data class Alumno(
    val id: String,
    val nombre: String,
    val grado: String,
    val direccion: String,
    val paradero: String,
    val horaEntrega: String? = null,
    val estado: EstadoEntrega = EstadoEntrega.PENDIENTE
)

data class PerfilConductor(
    val nombre: String,
    val gradoAsignado: String,
    val movilidad: String,
    val paradero: String,
    val contactoNombre: String,
    val contactoRol: String,
    val contactoTelefono: String
)

/** Perfil propio del conductor (datos de su cuenta y del vehículo). */
data class ConductorPerfil(
    val nombre: String,
    val celular: String,
    val correo: String,
    val contactoEmergencia: String,
    val dni: String,
    val licencia: String,
    val placa: String,
    val zona: String
)
