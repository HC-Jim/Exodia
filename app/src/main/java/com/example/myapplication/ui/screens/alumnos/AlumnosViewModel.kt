package com.example.myapplication.ui.screens.alumnos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utils.Sesion
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.Alumno
import com.example.myapplication.domain.EstadoEntrega
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Conductor (estudiantes a recoger).
 * Lee la lista desde la API (usuarios de su movilidad) y cambia su estado.
 */
class AlumnosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    // Lista de estudiantes de la ruta.
    var alumnos by mutableStateOf<List<Alumno>>(emptyList())
        private set

    // true mientras se consulta la API.
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error; null si no hubo problema.
    var error by mutableStateOf<String?>(null)
        private set

    init {
        cargar()
    }

    // Movilidad del conductor logueado; usa una por defecto si no hay sesión.
    private fun movilidad(): String = Sesion.usuario?.movilidad ?: "Movilidad N°04"

    fun cargar() {
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                alumnos = repo.obtenerEstudiantes(movilidad())
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    /** Marca al estudiante como ENTREGADO. */
    fun marcarEntregado(alumno: Alumno) {
        viewModelScope.launch {
            repo.marcarEntregado(alumno)
            cargar()
        }
    }

    /**
     * Busca al estudiante por el id leído del QR y lo marca como ENTREGADO.
     * Devuelve true si lo encontró (para dar feedback en la pantalla).
     */
    fun marcarEntregadoPorId(id: String): Boolean {
        val alumno = alumnos.find { it.id == id.trim() } ?: return false
        marcarEntregado(alumno)
        return true
    }

    /** Cancela el recojo del estudiante con un motivo. */
    fun cancelar(alumno: Alumno, motivo: String) {
        viewModelScope.launch {
            repo.cancelar(alumno, motivo)
            cargar()
        }
    }

    /** Reinicia la ruta: todos los estudiantes vuelven a PENDIENTE. */
    fun reiniciarRuta() {
        viewModelScope.launch {
            repo.reiniciarRuta(movilidad())
            cargar()
        }
    }

    // Estudiantes ya entregados (para el resumen de la pantalla de listado).
    val entregados: List<Alumno>
        get() {
            val lista = mutableListOf<Alumno>()
            for (a in alumnos) {
                if (a.estado == EstadoEntrega.ENTREGADO) {
                    lista.add(a)
                }
            }
            return lista
        }
}
