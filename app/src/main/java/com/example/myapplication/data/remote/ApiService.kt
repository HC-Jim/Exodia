package com.example.myapplication.data.remote

import com.example.myapplication.data.models.EstadoBody
import com.example.myapplication.data.models.LoginBody
import com.example.myapplication.data.models.PerfilBody
import com.example.myapplication.data.models.MensajeRespuesta
import com.example.myapplication.data.models.PreguntaRespuesta
import com.example.myapplication.data.models.ReinicioRespuesta
import com.example.myapplication.data.models.RestablecerBody
import com.example.myapplication.domain.Alumno
import com.example.myapplication.domain.CalendarioAsistencia
import com.example.myapplication.domain.Comunicado
import com.example.myapplication.domain.Nota
import com.example.myapplication.domain.Ubicacion
import com.example.myapplication.domain.Usuario
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
    suspend fun getComunicados(): List<Comunicado>

    // ---------- NOTAS ----------
    @GET("notas")
    suspend fun getNotas(): List<Nota>

    // ---------- USUARIOS (autenticación) ----------
    // El cuerpo del registro es un Usuario con las credenciales (solo de ida); la
    // respuesta es el usuario limpio (sin credenciales), que la app consume directo.
    @POST("usuarios/registrar")
    suspend fun registrar(@Body usuario: Usuario): Usuario

    @POST("usuarios/login")
    suspend fun login(@Body body: LoginBody): Usuario

    @GET("usuarios/pregunta/{correo}")
    suspend fun getPregunta(@Path("correo") correo: String): PreguntaRespuesta

    @POST("usuarios/restablecer")
    suspend fun restablecer(@Body body: RestablecerBody): MensajeRespuesta

    // Estudiantes de una movilidad (para el conductor). El servidor ya devuelve la forma "alumno".
    @GET("usuarios/estudiantes/{movilidad}")
    suspend fun getEstudiantes(@Path("movilidad") movilidad: String): List<Alumno>

    // Reinicia toda la ruta en el servidor (todos a PENDIENTE) en una sola llamada.
    @PUT("usuarios/estudiantes/{movilidad}/reiniciar")
    suspend fun reiniciarRuta(@Path("movilidad") movilidad: String): ReinicioRespuesta

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): Usuario

    @PUT("usuarios/{id}/estado")
    suspend fun actualizarEstadoUsuario(@Path("id") id: Long, @Body body: EstadoBody): Usuario

    // Actualiza los datos personales del perfil (nombre, celular, correo, direccion).
    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body body: PerfilBody): Usuario

    // ---------- ASISTENCIAS ----------
    // El servidor devuelve el calendario del mes ya armado (día -> estado).
    @GET("asistencias/{usuarioId}")
    suspend fun getAsistencias(@Path("usuarioId") usuarioId: Long): CalendarioAsistencia

    // ---------- UBICACIONES (seguimiento del bus) ----------
    // El apoderado lee la posición del bus.
    @GET("ubicaciones/{movilidad}")
    suspend fun getUbicacion(@Path("movilidad") movilidad: String): Ubicacion

    // El conductor envía su posición (upsert en el servidor).
    @PUT("ubicaciones/{movilidad}")
    suspend fun enviarUbicacion(
        @Path("movilidad") movilidad: String,
        @Body ubicacion: Ubicacion
    ): Ubicacion
}
