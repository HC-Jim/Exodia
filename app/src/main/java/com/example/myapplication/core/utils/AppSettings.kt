package com.example.myapplication.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Preferencias de accesibilidad/apariencia en memoria para el prototipo.
 *
 * Se expone como estado observable de Compose para que los ajustes tengan efecto
 * inmediato en toda la app. Al integrar la capa de datos migrará a DataStore
 * (persistencia entre sesiones), tal como indica la arquitectura del documento.
 */
object AppSettings {
    var modoOscuro by mutableStateOf(false)
    var escalaTexto by mutableFloatStateOf(1f)      // 0.85f .. 1.30f
    var mantenerPantalla by mutableStateOf(false)
    var silenciarNotificaciones by mutableStateOf(false)
}
