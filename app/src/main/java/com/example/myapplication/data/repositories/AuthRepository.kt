package com.example.myapplication.data.repositories

import com.example.myapplication.data.models.LoginBody
import com.example.myapplication.data.models.RestablecerBody
import com.example.myapplication.domain.Usuario
import com.example.myapplication.data.remote.RetrofitCliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de autenticación: login, registro y recuperación de contraseña.
 * Habla con los endpoints /usuarios de la API. El servidor ya devuelve el usuario
 * con la forma de la app, así que aquí no hay conversiones.
 */
class AuthRepository {

    private val api = RetrofitCliente.api

    /** Inicia sesión. Lanza excepción si las credenciales son inválidas. */
    suspend fun login(correo: String, contrasena: String): Usuario = withContext(Dispatchers.IO) {
        api.login(LoginBody(correo, contrasena))
    }

    /** Registra una cuenta nueva. Lanza excepción si el correo ya existe. */
    suspend fun registrar(usuario: Usuario): Usuario = withContext(Dispatchers.IO) {
        api.registrar(usuario)
    }

    /** Devuelve la pregunta de seguridad de un correo (null si no existe). */
    suspend fun obtenerPregunta(correo: String): String? = withContext(Dispatchers.IO) {
        api.getPregunta(correo).pregunta
    }

    /** Restablece la contraseña respondiendo la pregunta de seguridad. */
    suspend fun restablecer(correo: String, respuesta: String, nuevaContrasena: String) {
        withContext(Dispatchers.IO) {
            api.restablecer(RestablecerBody(correo, respuesta, nuevaContrasena))
        }
    }
}
