package com.example.myapplication.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.AppSettings
import com.example.myapplication.data.local.PreferenciasRepository
import kotlinx.coroutines.launch

/**
 * ViewModel de apariencia/accesibilidad (DataStore).
 *
 * Cada cambio hace dos cosas:
 *   1) Actualiza AppSettings -> el tema reacciona al instante en toda la app.
 *   2) Guarda en DataStore (con una corrutina) -> el ajuste se recuerda al reabrir.
 */
class ConfiguracionViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferenciasRepository(app)

    fun setModoOscuro(valor: Boolean) {
        AppSettings.modoOscuro = valor
        viewModelScope.launch { prefs.guardarModoOscuro(valor) }
    }

    fun setEscalaTexto(valor: Float) {
        AppSettings.escalaTexto = valor
        viewModelScope.launch { prefs.guardarEscalaTexto(valor) }
    }

    fun setMantenerPantalla(valor: Boolean) {
        AppSettings.mantenerPantalla = valor
        viewModelScope.launch { prefs.guardarMantenerPantalla(valor) }
    }

    fun setSilenciarNotificaciones(valor: Boolean) {
        AppSettings.silenciarNotificaciones = valor
        viewModelScope.launch { prefs.guardarSilenciarNotificaciones(valor) }
    }
}
