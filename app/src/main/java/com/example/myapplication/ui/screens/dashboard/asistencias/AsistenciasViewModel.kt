package com.example.myapplication.ui.screens.dashboard.asistencias

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.Sesion
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.entities.EstadoAsistencia
import kotlinx.coroutines.launch

/**
 * ViewModel de la asistencia del estudiante.
 *
 * Arma el calendario base (semana = clase, fin de semana = sin clase) y le
 * aplica encima los días especiales (falta/tardanza/justificado) que trae la API.
 */
class AsistenciasViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    // Datos fijos del mes que dibuja el calendario.
    val nombreMes = "Septiembre 2026"
    val diasDelMes = 30
    val offsetPrimerDia = 2   // 1 de septiembre cae en martes (0=Dom)

    // Estado de cada día: día del mes -> estado de asistencia.
    var asistencia by mutableStateOf<Map<Int, EstadoAsistencia>>(emptyMap())
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            // Primero el calendario "normal", luego los días especiales de la API encima.
            val base = construirBase()
            val usuarioId = Sesion.usuario?.id
            val excepciones = if (usuarioId != null) repo.obtenerAsistencias(usuarioId) else emptyMap()
            // Las excepciones de la API pisan al calendario base.
            asistencia = base + excepciones
        }
    }

    // Calendario base: días de semana como PRESENTE y fines de semana como SIN_CLASE.
    private fun construirBase(): Map<Int, EstadoAsistencia> {
        val mapa = mutableMapOf<Int, EstadoAsistencia>()
        for (dia in 1..diasDelMes) {
            val semana = (dia + offsetPrimerDia - 1) % 7  // 0=Dom ... 6=Sab
            if (semana == 0 || semana == 6) {
                mapa[dia] = EstadoAsistencia.SIN_CLASE
            } else {
                mapa[dia] = EstadoAsistencia.PRESENTE
            }
        }
        return mapa
    }
}
