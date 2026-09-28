package com.example.myapplication.domain

import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.WarningAmber

/**
 * Entidades de negocio del rol Apoderado.
 */

/**
 * Aviso o comunicado del colegio.
 */
data class Comunicado(
    val id: Long = 0,
    val titulo: String = "",
    val detalle: String? = null,
    val fecha: String? = null
)

/**
 * Calificación de un curso (valor es el número mostrado).
 */
data class Nota(
    val id: Long = 0,
    val curso: String = "",
    val detalle: String? = null,
    val valor: String? = null
)

/** Estado de un día en el calendario de asistencia, con su etiqueta y color para la UI. */
enum class EstadoAsistencia(val etiqueta: String, val color: Color) {
    PRESENTE("Presente", SuccessGreen),
    TARDANZA("Tardanza", WarningAmber),
    FALTA("Falta", Color(0xFFEF4444)),
    JUSTIFICADO("Justificado", AccentBlue),
    SIN_CLASE("Sin clase", Color(0xFFCBD2E0))
}

/**
 * Calendario de asistencia de un mes, tal como lo arma el servidor.
 */
data class CalendarioAsistencia(
    val mes: String,
    val diasDelMes: Int,
    val offsetPrimerDia: Int,
    val asistencia: Map<Int, EstadoAsistencia>
)
