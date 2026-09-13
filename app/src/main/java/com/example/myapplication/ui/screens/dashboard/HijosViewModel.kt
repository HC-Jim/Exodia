package com.example.myapplication.ui.screens.dashboard

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.entities.Hijo
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Apoderado (lista de hijos que se muestra en el inicio).
 */
class HijosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    var hijos by mutableStateOf<List<Hijo>>(emptyList())
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
                hijos = repo.obtenerHijos()
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }
}
