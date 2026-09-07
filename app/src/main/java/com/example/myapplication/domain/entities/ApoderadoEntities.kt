package com.example.myapplication.domain.entities

import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.WarningAmber

/**
 * Entidades de negocio del rol Apoderado.
 */

data class Hijo(
    val id: String,
    val nombre: String,
    val grado: String,
    val movilidad: String,
    val paradero: String,
    val contactoNombre: String,
    val contactoRol: String,
    val color: Color
)

data class Comunicado(
    val id: String,
    val titulo: String,
    val detalle: String,
    val fecha: String,
    val color: Color
)

data class Nota(
    val id: String,
    val curso: String,
    val detalle: String,
    val valor: String,
    val color: Color
)

enum class EstadoAsistencia(val etiqueta: String, val color: Color) {
    PRESENTE("Presente", SuccessGreen),
    TARDANZA("Tardanza", WarningAmber),
    FALTA("Falta", Color(0xFFEF4444)),
    JUSTIFICADO("Justificado", AccentBlue),
    SIN_CLASE("Sin clase", Color(0xFFCBD2E0))
}

/** Un paso del seguimiento del transporte (línea de tiempo). */
data class PasoRuta(
    val titulo: String,
    val detalle: String,
    val hora: String,
    val completado: Boolean
)
