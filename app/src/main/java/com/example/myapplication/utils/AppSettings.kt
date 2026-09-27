package com.example.myapplication.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Preferencias de accesibilidad/apariencia en memoria para el prototipo.
**/
object AppSettings {
    var modoOscuro by mutableStateOf(false)
    var escalaTexto by mutableFloatStateOf(1f)      // 0.85f .. 1.30f
    var silenciarNotificaciones by mutableStateOf(false)
}
