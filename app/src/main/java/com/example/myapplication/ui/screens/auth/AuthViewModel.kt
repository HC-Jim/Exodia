package com.example.myapplication.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.utils.Sesion
import com.example.myapplication.data.models.UsuarioDto
import com.example.myapplication.data.repositories.AuthRepository
import kotlinx.coroutines.launch

/**
 * ViewModel de autenticación (login, registro y recuperación de contraseña).
 * Guarda el usuario en Sesion cuando el login es correcto.
 */
class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var pregunta by mutableStateOf<String?>(null)   // pregunta de seguridad recuperada
        private set

    fun limpiarError() { error = null }

    /** Inicia sesión; si es correcto, llama onExito(). */
    fun login(correo: String, contrasena: String, onExito: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            error = "Ingresa tu correo y contraseña"
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                Sesion.usuario = repo.login(correo.trim(), contrasena)
                onExito()
            } catch (e: Exception) {
                error = "Correo o contraseña incorrectos"
            } finally {
                cargando = false
            }
        }
    }

    /** Registra una cuenta; si es correcto, llama onExito(). */
    fun registrar(usuario: UsuarioDto, onExito: () -> Unit) {
        if (usuario.nombre.isNullOrBlank() || usuario.correo.isNullOrBlank() || usuario.contrasena.isNullOrBlank()) {
            error = "Completa nombre, correo y contraseña"
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                repo.registrar(usuario)
                onExito()
            } catch (e: Exception) {
                error = "No se pudo registrar (¿el correo ya existe?)"
            } finally {
                cargando = false
            }
        }
    }

    /** Busca la pregunta de seguridad de un correo. */
    fun cargarPregunta(correo: String) {
        if (correo.isBlank()) {
            error = "Ingresa tu correo"
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            pregunta = null
            try {
                pregunta = repo.obtenerPregunta(correo.trim())
                if (pregunta == null) error = "No se encontró la cuenta"
            } catch (e: Exception) {
                error = "No existe una cuenta con ese correo"
            } finally {
                cargando = false
            }
        }
    }

    /** Restablece la contraseña; si es correcto, llama onExito(). */
    fun restablecer(correo: String, respuesta: String, nuevaContrasena: String, onExito: () -> Unit) {
        if (respuesta.isBlank() || nuevaContrasena.isBlank()) {
            error = "Responde la pregunta y escribe la nueva contraseña"
            return
        }
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                repo.restablecer(correo.trim(), respuesta, nuevaContrasena)
                onExito()
            } catch (e: Exception) {
                error = "La respuesta de seguridad no coincide"
            } finally {
                cargando = false
            }
        }
    }
}
