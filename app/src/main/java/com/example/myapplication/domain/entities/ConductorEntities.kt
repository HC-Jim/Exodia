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
