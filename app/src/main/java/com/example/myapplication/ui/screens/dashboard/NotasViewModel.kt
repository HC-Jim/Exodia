package com.example.myapplication.ui.screens.dashboard

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.entities.Nota
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Apoderado (pantalla de Notas).
 * Con caché offline vía DatosRepository (mismo patrón que Comunicados).
 */
class NotasViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    var notas by mutableStateOf<List<Nota>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        cargar()
    }

    fun cargar() {
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
