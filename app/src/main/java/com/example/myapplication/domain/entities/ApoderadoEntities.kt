package com.example.myapplication.domain.entities

import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.WarningAmber

/**
 * Entidades de negocio del rol Apoderado.
 */

/** Aviso o comunicado del colegio. El color se usa para distinguir el tipo en la lista. */
data class Comunicado(
    val id: String,
    val titulo: String,
    val detalle: String,
    val fecha: String,
    val color: Color
)

/** Calificación de un curso. valor es el número mostrado; color según el rango de la nota. */
data class Nota(
    val id: String,
    val curso: String,
    val detalle: String,
    val valor: String,
    val color: Color
)

/** Estado de un día en el calendario de asistencia, con su etiqueta y color para la UI. */
enum class EstadoAsistencia(val etiqueta: String, val color: Color) {
    PRESENTE("Presente", SuccessGreen),
    TARDANZA("Tardanza", WarningAmber),
    FALTA("Falta", Color(0xFFEF4444)),
    JUSTIFICADO("Justificado", AccentBlue),
    SIN_CLASE("Sin clase", Color(0xFFCBD2E0))
}
