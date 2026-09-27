package com.example.myapplication.ui.screens.asistencias

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utils.Sesion
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.CalendarioAsistencia
import com.example.myapplication.domain.EstadoAsistencia
import kotlinx.coroutines.launch

/**
 * ViewModel de la asistencia del estudiante.
 *
 * Ya NO arma el calendario: lo pide completo al servidor y solo lo expone.
 * (La regla de qué días hay clase vive en el backend.)
 */
class AsistenciasViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    // El calendario tal como lo devuelve el servidor (null mientras carga).
    private var calendario by mutableStateOf<CalendarioAsistencia?>(null)

    // Propiedades que la pantalla ya usaba, ahora derivadas del calendario del servidor.
    val nombreMes: String get() = calendario?.mes ?: ""
    val diasDelMes: Int get() = calendario?.diasDelMes ?: 0
    val offsetPrimerDia: Int get() = calendario?.offsetPrimerDia ?: 0
    val asistencia: Map<Int, EstadoAsistencia> get() = calendario?.asistencia ?: emptyMap()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            val usuarioId = Sesion.usuario?.id
            if (usuarioId != null) {
                calendario = repo.obtenerAsistencias(usuarioId)
            }
        }
    }
}
