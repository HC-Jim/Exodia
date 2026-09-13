package com.example.myapplication.ui.screens.dashboard

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.EstadoEntrega
import com.example.myapplication.domain.entities.RegistroHistorial
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Conductor (ruta y entregas).
 *
 * Además de listar alumnos (con caché offline), soporta:
 *   - marcar un alumno como ENTREGADO (offline-first),
 *   - contar acciones pendientes de sincronizar,
 *   - sincronizar la cola cuando vuelve el internet,
 *   - mostrar el historial local de entregas.
 */
class AlumnosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    var alumnos by mutableStateOf<List<Alumno>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var pendientes by mutableStateOf(0)          // acciones sin sincronizar
        private set

    var historial by mutableStateOf<List<RegistroHistorial>>(emptyList())
        private set

    var mensaje by mutableStateOf<String?>(null) // aviso tras sincronizar
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                alumnos = repo.obtenerAlumnos()
                pendientes = repo.contarPendientes()
                historial = repo.obtenerHistorial()
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    /** Marca al alumno como ENTREGADO (guarda local + intenta enviar). */
    fun marcarEntregado(alumno: Alumno) {
        viewModelScope.launch {
            repo.marcarEntregado(alumno)
            cargar()
        }
    }

    /** Envía a la API todas las entregas que quedaron en cola. */
    fun sincronizar() {
        viewModelScope.launch {
            val enviadas = repo.sincronizar()
            mensaje = if (enviadas > 0) "$enviadas cambio(s) sincronizado(s)"
            else "No hay conexión o nada que enviar"
            cargar()
        }
    }

    fun limpiarMensaje() { mensaje = null }

    // Alumnos ya entregados (para la pantalla de listado).
    val entregados: List<Alumno>
        get() = alumnos.filter { it.estado == EstadoEntrega.ENTREGADO }

    // Próximo alumno por entregar (para la tarjeta de ruta activa).
    val proximaEntrega: Alumno?
        get() = alumnos.firstOrNull { it.estado != EstadoEntrega.ENTREGADO }
}
