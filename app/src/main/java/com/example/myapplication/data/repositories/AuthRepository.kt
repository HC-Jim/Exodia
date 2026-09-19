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
// withContext(Dispatchers.IO = ejecutar en segundo plano
    /** Inicia sesión. Lanza excepción si las credenciales son inválidas. */
    suspend fun login(correo: String, contrasena: String): Usuario {
        val usuario = withContext(Dispatchers.IO) {
            val dto = api.login(LoginBody(correo, contrasena))
            aDominio(dto)          // esto queda guardado en 'usuario'
        }
        return usuario             // return explícito al final, como en Java
    }

    /** Registra una cuenta nueva. Lanza excepción si el correo ya existe. */
    suspend fun registrar(usuario: UsuarioDto): Usuario {
        val creado = withContext(Dispatchers.IO) {
            val dto = api.registrar(usuario)
            aDominio(dto)          // queda guardado en 'creado'
        }
        return creado              // return explícito al final
    }

    /** Devuelve la pregunta de seguridad de un correo (null si no existe). */
    suspend fun obtenerPregunta(correo: String): String? {
        val pregunta = withContext(Dispatchers.IO) {
            val respuesta = api.getPregunta(correo)
            respuesta.pregunta     // queda guardado en 'pregunta'
        }
        return pregunta            // return explícito al final
    }

    /** Restablece la contraseña respondiendo la pregunta de seguridad. */
    suspend fun restablecer(correo: String, respuesta: String, nuevaContrasena: String) {
        withContext(Dispatchers.IO) {
            val body = RestablecerBody(correo, respuesta, nuevaContrasena)
            api.restablecer(body)
        }
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
            lng = dto.lng,
            celular = dto.celular,
            contactoEmergencia = dto.contactoEmergencia,
            dni = dto.dni,
            licencia = dto.licencia,
            placa = dto.placa,
            zona = dto.zona,
            estado = dto.estado
        )
    }
}
