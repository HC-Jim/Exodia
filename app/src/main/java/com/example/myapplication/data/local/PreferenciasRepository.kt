package com.example.myapplication.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// Crea UN solo DataStore llamado "ajustes" para toda la app.
private val Context.dataStore by preferencesDataStore(name = "ajustes")

/** Datos de apariencia leídos de una sola vez (para restaurar al iniciar). */
data class AjustesGuardados(
    val modoOscuro: Boolean,
    val escalaTexto: Float,
    val mantenerPantalla: Boolean,
    val silenciarNotificaciones: Boolean
)

/**
 * Repositorio de preferencias de apariencia usando DataStore.
 *
 * DataStore es el reemplazo moderno de SharedPreferences: guarda pares
 * clave-valor de forma asíncrona (con corrutinas), por eso los métodos son
 * suspend. Los datos sobreviven aunque se cierre la app.
 */
class PreferenciasRepository(private val context: Context) {

    companion object {
        private val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        private val ESCALA_TEXTO = floatPreferencesKey("escala_texto")
        private val MANTENER = booleanPreferencesKey("mantener_pantalla")
        private val SILENCIAR = booleanPreferencesKey("silenciar_notificaciones")
    }

    // ---- Guardar (cada uno escribe una clave) ----
    suspend fun guardarModoOscuro(valor: Boolean) {
        context.dataStore.edit { ajustes -> ajustes[MODO_OSCURO] = valor }
    }

    suspend fun guardarEscalaTexto(valor: Float) {
        context.dataStore.edit { ajustes -> ajustes[ESCALA_TEXTO] = valor }
    }

    suspend fun guardarMantenerPantalla(valor: Boolean) {
        context.dataStore.edit { ajustes -> ajustes[MANTENER] = valor }
    }

    suspend fun guardarSilenciarNotificaciones(valor: Boolean) {
        context.dataStore.edit { ajustes -> ajustes[SILENCIAR] = valor }
    }

    // ---- Leer todo de una vez (con valores por defecto si aún no hay nada) ----
    suspend fun leerAjustes(): AjustesGuardados {
        val ajustes = context.dataStore.data.first()
        return AjustesGuardados(
            modoOscuro = ajustes[MODO_OSCURO] ?: false,
            escalaTexto = ajustes[ESCALA_TEXTO] ?: 1f,
            mantenerPantalla = ajustes[MANTENER] ?: false,
            silenciarNotificaciones = ajustes[SILENCIAR] ?: false
        )
    }
}
