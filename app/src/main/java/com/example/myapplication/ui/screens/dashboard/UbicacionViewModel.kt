package com.example.myapplication.ui.screens.dashboard

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.domain.entities.Ubicacion
import kotlinx.coroutines.launch

/**
 * ViewModel del seguimiento del bus.
 *
 *  - APODERADO: llama a [refrescar] cada pocos segundos para leer la posición
 *    del bus y mostrarla en el mapa.
 *  - CONDUCTOR: llama a [enviar] con su posición GPS para publicarla.
 *
 * (El envío periódico del GPS y el mapa se conectan en el paso 3.)
 */
class UbicacionViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    var ubicacion by mutableStateOf<Ubicacion?>(null)
        private set

    /** Lee la última posición del bus de esa movilidad. */
    fun refrescar(movilidad: String) {
        viewModelScope.launch {
            ubicacion = repo.obtenerUbicacion(movilidad)
        }
    }

    /** Publica la posición actual del conductor. */
    fun enviar(movilidad: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            repo.enviarUbicacion(movilidad, lat, lng)
        }
    }
}
