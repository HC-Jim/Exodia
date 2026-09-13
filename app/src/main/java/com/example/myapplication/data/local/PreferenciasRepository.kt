package com.example.myapplication.data.local

import android.content.Context

/**
 * Repositorio de preferencias de apariencia usando SharedPreferences.
 *
 * SharedPreferences guarda pares clave-valor en un XML privado de la app,
 * ideal para ajustes simples (modo oscuro, tamaño de letra, switches).
 * Los datos sobreviven aunque se cierre la app.
 *
 * (DataStore sería la alternativa moderna a SharedPreferences; aquí usamos
 *  SharedPreferences por su sintaxis directa.)
 */
class PreferenciasRepository(context: Context) {

    private val prefs = context.getSharedPreferences("ajustes_appescolar", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MODO_OSCURO = "modo_oscuro"
        private const val KEY_ESCALA_TEXTO = "escala_texto"
        private const val KEY_MANTENER = "mantener_pantalla"
        private const val KEY_SILENCIAR = "silenciar_notificaciones"
    }

    // ---- Guardar (escribe y confirma con apply(), que es asíncrono) ----
    fun guardarModoOscuro(valor: Boolean) =
        prefs.edit().putBoolean(KEY_MODO_OSCURO, valor).apply()

    fun guardarEscalaTexto(valor: Float) =
        prefs.edit().putFloat(KEY_ESCALA_TEXTO, valor).apply()

    fun guardarMantenerPantalla(valor: Boolean) =
        prefs.edit().putBoolean(KEY_MANTENER, valor).apply()

    fun guardarSilenciarNotificaciones(valor: Boolean) =
        prefs.edit().putBoolean(KEY_SILENCIAR, valor).apply()

    // ---- Leer (con valor por defecto si aún no se ha guardado nada) ----
    fun leerModoOscuro(): Boolean = prefs.getBoolean(KEY_MODO_OSCURO, false)
    fun leerEscalaTexto(): Float = prefs.getFloat(KEY_ESCALA_TEXTO, 1f)
    fun leerMantenerPantalla(): Boolean = prefs.getBoolean(KEY_MANTENER, false)
    fun leerSilenciarNotificaciones(): Boolean = prefs.getBoolean(KEY_SILENCIAR, false)
}
