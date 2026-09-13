package com.example.myapplication.ui.screens.dashboard

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.entities.Comunicado
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Apoderado (pantalla de Comunicados).
 *
 * Usa DatosRepository, que trae los comunicados de la API y los guarda en la
 * caché local (SQLite). Si no hay internet, muestra la última copia guardada.
 */
class ComunicadosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    var comunicados by mutableStateOf<List<Comunicado>>(emptyList())
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
                comunicados = repo.obtenerComunicados()
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }
}
