package com.example.myapplication.ui.screens.notas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.Nota
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Apoderado (pantalla de Notas).
 * Con caché offline vía DatosRepository (mismo patrón que Comunicados).
 */
class NotasViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    // Lista de notas que muestra la pantalla. private set: solo el ViewModel la cambia.
    var notas by mutableStateOf<List<Nota>>(emptyList())
        private set

    // true mientras se consulta la fuente de datos.
    var cargando by mutableStateOf(false)
        private set

    // Texto de error cuando la carga falla; null si todo salió bien.
    var error by mutableStateOf<String?>(null)
        private set

    // Carga las notas al crear el ViewModel.
    init {
        cargar()
    }

    fun cargar() {
        // viewModelScope: la corrutina se cancela sola si se destruye el ViewModel.
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                notas = repo.obtenerNotas()
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }
}
