package com.example.myapplication.ui.screens.comunicados

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.Comunicado
import kotlinx.coroutines.launch

/**
 * ViewModel del rol Apoderado (pantalla de Comunicados).
 *
 * Usa DatosRepository, que trae los comunicados de la API y los guarda en la
 * caché local (SQLite). Si no hay internet, muestra la última copia guardada.
 */
class ComunicadosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    // Lista que muestra la pantalla.
    var comunicados by mutableStateOf<List<Comunicado>>(emptyList())
        private set

    // true mientras se consulta la fuente de datos.
    var cargando by mutableStateOf(false)
        private set

    // Mensaje de error; null si no hubo problema.
    var error by mutableStateOf<String?>(null)
        private set

    // La pantalla llama a cargar() al entrar (LaunchedEffect), por eso no se carga en init
    // (así se evita hacer la primera petición dos veces).
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
