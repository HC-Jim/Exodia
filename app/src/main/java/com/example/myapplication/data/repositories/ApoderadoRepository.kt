package com.example.myapplication.data.repositories

import com.example.myapplication.domain.entities.Comunicado
import com.example.myapplication.domain.entities.EstadoAsistencia
import com.example.myapplication.domain.entities.Hijo
import com.example.myapplication.domain.entities.Nota
import com.example.myapplication.domain.entities.PasoRuta
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.AccentPink
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.WarningAmber

/**
 * Fuente de datos de ejemplo para el rol Apoderado.
 * Repositorio temporal mientras no exista backend.
 */
object MockApoderado {

    const val nombrePadre = "Marco Zuñiga"

    val julio = Hijo(
        id = "julio",
        nombre = "Julio Zuñiga",
        grado = "5° Primaria",
        movilidad = "Movilidad N°04",
        paradero = "Paradero Av. Principal",
        contactoNombre = "Carlos García",
        contactoRol = "Conductor",
        color = AccentBlue
    )

    val maria = Hijo(
        id = "maria",
        nombre = "María Zuñiga",
        grado = "1° Primaria",
        movilidad = "Movilidad N°04",
        paradero = "Paradero Av. Principal",
        contactoNombre = "Carlos García",
        contactoRol = "Conductor",
        color = AccentPink
    )

    val hijos = listOf(julio, maria)

    val comunicados = listOf(
        Comunicado("1", "Reunión de apoderados", "Aula 5° Primaria · 6:00 p.m.", "Lun 23", IndigoPrimary),
        Comunicado("2", "Simulacro de sismo", "Toda la institución", "Mar 24", WarningAmber),
        Comunicado("3", "Entrega de libretas", "Auditorio principal", "Mié 25", AccentBlue),
        Comunicado("4", "Feria de ciencias", "Patio central · 10:00 a.m.", "Jue 26", SuccessGreen),
        Comunicado("5", "Día del logro", "Presentación por grados", "Vie 27", AccentPink)
    )

    val notas = listOf(
        Nota("1", "Matemática", "Examen bimestral", "18", AccentBlue),
        Nota("2", "Comunicación", "Comprensión lectora", "17", AccentPink),
        Nota("3", "Ciencia y Tecnología", "Proyecto de feria", "20", SuccessGreen),
        Nota("4", "Personal Social", "Trabajo grupal", "16", WarningAmber),
        Nota("5", "Arte y Cultura", "Exposición", "19", IndigoPrimary)
    )

    val pasosRuta = listOf(
        PasoRuta("Vehículo en ruta", "El bus inició su recorrido", "07:00", true),
        PasoRuta("Alumno recogido", "Julio abordó el vehículo", "07:18", true),
        PasoRuta("Camino al colegio", "En tránsito hacia la institución", "07:25", true),
        PasoRuta("Llegada al colegio", "Entrega estimada", "07:45", false)
    )

    // Asistencia de ejemplo para el mes (día -> estado).
    val nombreMes = "Septiembre 2026"
    const val diasDelMes = 30
    const val offsetPrimerDia = 2 // 1 de septiembre cae en martes (0=Dom)
    val asistencia: Map<Int, EstadoAsistencia> = buildMap {
        for (dia in 1..diasDelMes) {
            val semana = (dia + offsetPrimerDia - 1) % 7 // 0=Dom ... 6=Sab
            put(
                dia,
                when {
                    semana == 0 || semana == 6 -> EstadoAsistencia.SIN_CLASE
                    dia == 8 -> EstadoAsistencia.FALTA
                    dia == 15 -> EstadoAsistencia.TARDANZA
                    dia == 22 -> EstadoAsistencia.JUSTIFICADO
                    else -> EstadoAsistencia.PRESENTE
                }
            )
        }
    }
}
