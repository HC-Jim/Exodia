package com.example.myapplication.data.repositories

import com.example.myapplication.data.models.LoginBody
import com.example.myapplication.data.models.RestablecerBody
import com.example.myapplication.data.models.UsuarioDto
import com.example.myapplication.domain.entities.Usuario
import com.example.myapplication.services.api.RetrofitCliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de autenticación: login, registro y recuperación de contraseña.
 * Habla con los endpoints /usuarios de la API.
 */
class AuthRepository {

    private val api = RetrofitCliente.api

    /** Inicia sesión. Lanza excepción si las credenciales son inválidas. */
    suspend fun login(correo: String, contrasena: String): Usuario = withContext(Dispatchers.IO) {
        val dto = api.login(LoginBody(correo, contrasena))
        aDominio(dto)
    }

    /** Registra una cuenta nueva. Lanza excepción si el correo ya existe. */
    suspend fun registrar(usuario: UsuarioDto): Usuario = withContext(Dispatchers.IO) {
        val dto = api.registrar(usuario)
        aDominio(dto)
    }

    /** Devuelve la pregunta de seguridad de un correo (null si no existe). */
    suspend fun obtenerPregunta(correo: String): String? = withContext(Dispatchers.IO) {
        api.getPregunta(correo).pregunta
    }

    /** Restablece la contraseña respondiendo la pregunta de seguridad. */
    suspend fun restablecer(correo: String, respuesta: String, nuevaContrasena: String) =
        withContext(Dispatchers.IO) {
            api.restablecer(RestablecerBody(correo, respuesta, nuevaContrasena))
            Unit
        }

    // Convierte el DTO de la API a la entidad de dominio.
    private fun aDominio(dto: UsuarioDto): Usuario {
        return Usuario(
            id = dto.id,
            nombre = dto.nombre ?: "",
            correo = dto.correo ?: "",
            rol = dto.rol ?: "ESTUDIANTE",
            estudianteNombre = dto.estudianteNombre,
            estudianteGrado = dto.estudianteGrado,
            movilidad = dto.movilidad,
            lat = dto.lat,
            lng = dto.lng
        )
    }
}
