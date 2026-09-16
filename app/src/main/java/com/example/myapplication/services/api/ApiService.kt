package com.example.myapplication.services.api

import com.example.myapplication.data.models.ComunicadoDto
import com.example.myapplication.data.models.AsistenciaDto
import com.example.myapplication.data.models.EstadoBody
import com.example.myapplication.data.models.LoginBody
import com.example.myapplication.data.models.MensajeRespuesta
import com.example.myapplication.data.models.NotaDto
import com.example.myapplication.data.models.PreguntaRespuesta
import com.example.myapplication.data.models.RestablecerBody
import com.example.myapplication.data.models.UbicacionDto
import com.example.myapplication.data.models.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz que describe la API REST. Retrofit la implementa solo.
 * Cada función es una llamada HTTP; "suspend" permite usarla con corrutinas.
 */
interface ApiService {

    // ---------- COMUNICADOS ----------
    @GET("comunicados")
    suspend fun getComunicados(): List<ComunicadoDto>

    // ---------- NOTAS ----------
    @GET("notas")
    suspend fun getNotas(): List<NotaDto>

    // ---------- USUARIOS (autenticación) ----------
    @POST("usuarios/registrar")
    suspend fun registrar(@Body usuario: UsuarioDto): UsuarioDto

    @POST("usuarios/login")
    suspend fun login(@Body body: LoginBody): UsuarioDto

    @GET("usuarios/pregunta/{correo}")
    suspend fun getPregunta(@Path("correo") correo: String): PreguntaRespuesta

    @POST("usuarios/restablecer")
    suspend fun restablecer(@Body body: RestablecerBody): MensajeRespuesta

    // Estudiantes de una movilidad (para el conductor) y cambio de estado
    @GET("usuarios/estudiantes/{movilidad}")
    suspend fun getEstudiantes(@Path("movilidad") movilidad: String): List<UsuarioDto>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): UsuarioDto

    @PUT("usuarios/{id}/estado")
    suspend fun actualizarEstadoUsuario(@Path("id") id: Long, @Body body: EstadoBody): UsuarioDto

    // ---------- ASISTENCIAS ----------
    @GET("asistencias/{usuarioId}")
    suspend fun getAsistencias(@Path("usuarioId") usuarioId: Long): List<AsistenciaDto>

    // ---------- UBICACIONES (seguimiento del bus) ----------
    // El apoderado lee la posición del bus.
    @GET("ubicaciones/{movilidad}")
    suspend fun getUbicacion(@Path("movilidad") movilidad: String): UbicacionDto

    // El conductor envía su posición (upsert en el servidor).
    @PUT("ubicaciones/{movilidad}")
    suspend fun enviarUbicacion(
        @Path("movilidad") movilidad: String,
        @Body ubicacion: UbicacionDto
    ): UbicacionDto
}
