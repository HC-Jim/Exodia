package com.example.myapplication.conductor.model

/**
 * Modelos de presentación para el rol Conductor.
 *
 * Por ahora solo alimentan las interfaces con datos de ejemplo. Cuando se
 * incorpore la capa de datos (Room + REST + Firestore, sección 4 del documento),
 * estos modelos pasarán a mapearse desde el dominio en los ViewModel.
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

/** Datos de ejemplo para poblar las pantallas mientras no hay backend. */
object MockConductor {

    val perfil = PerfilConductor(
        nombre = "Julio Zuñiga",
        gradoAsignado = "5° Primaria",
        movilidad = "Movilidad N°04",
        paradero = "Paradero Av. Principal",
        contactoNombre = "Carlos García",
        contactoRol = "Conductor",
        contactoTelefono = "+51 987 654 321"
    )

    val nombreCorto = "Marco Zuñiga"

    val alumnos = listOf(
        Alumno("1", "José Fernández", "5° Prim.", "Av. Los Álamos 145", "Paradero 1", "07:12", EstadoEntrega.ENTREGADO),
        Alumno("2", "Sofía Ramírez", "5° Prim.", "Calle Magnolia 128", "Paradero 2", "07:18", EstadoEntrega.ENTREGADO),
        Alumno("3", "Martín López", "5° Prim.", "Jr. Las Flores 90", "Paradero 2", "07:21", EstadoEntrega.ENTREGADO),
        Alumno("4", "Laura Torres", "5° Prim.", "Av. Central 501", "Paradero 3", "07:26", EstadoEntrega.ENTREGADO),
        Alumno("5", "Diego Castro", "5° Prim.", "Calle Los Pinos 33", "Paradero 3", "07:30", EstadoEntrega.ENTREGADO),
        Alumno("6", "Camila Rojas", "5° Prim.", "Av. La Merced 12", "Paradero 4", "07:34", EstadoEntrega.ENTREGADO),
        Alumno("7", "Lucas Fernández", "5° Prim.", "Jr. Amazonas 210", "Paradero 4", "07:39", EstadoEntrega.ENTREGADO),
        Alumno("8", "Valeria Gómez", "5° Prim.", "Calle Sol 8", "Paradero 5", "07:43", EstadoEntrega.ENTREGADO),
        Alumno("9", "Andrés Vega", "5° Prim.", "Av. Primavera 77", "Paradero 5", "07:47", EstadoEntrega.ENTREGADO),
        Alumno("10", "Daniela Ruiz", "5° Prim.", "Jr. Union 145", "Paradero 6", "07:51", EstadoEntrega.ENTREGADO),
        Alumno("11", "Sebastián León", "5° Prim.", "Calle Bolívar 4", "Paradero 6", null, EstadoEntrega.ABORDO),
        Alumno("12", "Ariana Morán", "5° Prim.", "Av. Grau 320", "Paradero 7", null, EstadoEntrega.ABORDO),
        Alumno("13", "Mateo Vargas", "5° Prim.", "Jr. Ayacucho 58", "Paradero 7", null, EstadoEntrega.PENDIENTE)
    )

    /** Próxima entrega pendiente (para la tarjeta de la ruta activa). */
    val proximaEntrega: Alumno = alumnos.first { it.estado != EstadoEntrega.ENTREGADO }

    val totalAlumnos: Int = alumnos.size
    val entregados: Int get() = alumnos.count { it.estado == EstadoEntrega.ENTREGADO }
}
