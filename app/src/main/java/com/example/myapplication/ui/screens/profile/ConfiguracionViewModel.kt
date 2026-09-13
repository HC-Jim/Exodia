package com.example.myapplication.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.core.utils.AppSettings
import com.example.myapplication.data.local.PreferenciasRepository

/**
 * ViewModel de apariencia/accesibilidad (SharedPreferences).
 *
 * Usa AndroidViewModel para obtener el Context de la aplicación y construir
 * el repositorio. Cada cambio hace dos cosas:
 *   1) Actualiza AppSettings -> el tema reacciona al instante en toda la app.
 *   2) Persiste en SharedPreferences -> el ajuste se recuerda al reabrir.
 */
class ConfiguracionViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferenciasRepository(app)

    fun setModoOscuro(valor: Boolean) {
        AppSettings.modoOscuro = valor
        prefs.guardarModoOscuro(valor)
    }

    fun setEscalaTexto(valor: Float) {
        AppSettings.escalaTexto = valor
        prefs.guardarEscalaTexto(valor)
    }

    fun setMantenerPantalla(valor: Boolean) {
        AppSettings.mantenerPantalla = valor
        prefs.guardarMantenerPantalla(valor)
    }

    fun setSilenciarNotificaciones(valor: Boolean) {
        AppSettings.silenciarNotificaciones = valor
        prefs.guardarSilenciarNotificaciones(valor)
    }
}
