package com.example.myapplication.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.domain.Usuario

/**
 * Sesión actual de la app (usuario que inició sesión).
 */
object Sesion {
    var usuario by mutableStateOf<Usuario?>(null)
}
