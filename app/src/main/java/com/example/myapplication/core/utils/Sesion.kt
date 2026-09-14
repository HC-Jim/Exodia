package com.example.myapplication.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.domain.entities.Usuario

/**
 * Sesión actual de la app (usuario que inició sesión).
 * Es un estado observable de Compose, disponible en toda la app.
 */
object Sesion {
    var usuario by mutableStateOf<Usuario?>(null)
}
