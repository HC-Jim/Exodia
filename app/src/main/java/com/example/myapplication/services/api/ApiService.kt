package com.example.myapplication.services.api

import com.example.myapplication.data.models.AlumnoDto
import com.example.myapplication.data.models.ComunicadoDto
import com.example.myapplication.data.models.HijoDto
import com.example.myapplication.data.models.NotaDto
import com.example.myapplication.data.models.UbicacionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Interfaz que describe la API REST. Retrofit la implementa solo.
 * Cada función es una llamada HTTP; "suspend" permite usarla con corrutinas.
 */
interface ApiService {

    // ---------- ALUMNOS ----------
    @GET("alumnos")
    suspend fun getAlumnos(): List<AlumnoDto>

    @GET("alumnos/{id}")
    suspend fun getAlumno(@Path("id") id: Long): AlumnoDto

    @POST("alumnos")
    suspend fun crearAlumno(@Body alumno: AlumnoDto): AlumnoDto

    @PUT("alumnos/{id}")
    suspend fun actualizarAlumno(@Path("id") id: Long, @Body alumno: AlumnoDto): AlumnoDto

    @DELETE("alumnos/{id}")
    suspend fun borrarAlumno(@Path("id") id: Long)

    // ---------- COMUNICADOS ----------
    @GET("comunicados")
    suspend fun getComunicados(): List<ComunicadoDto>

    @GET("comunicados/{id}")
    suspend fun getComunicado(@Path("id") id: Long): ComunicadoDto

    @POST("comunicados")
    suspend fun crearComunicado(@Body comunicado: ComunicadoDto): ComunicadoDto

    @PUT("comunicados/{id}")
    suspend fun actualizarComunicado(@Path("id") id: Long, @Body comunicado: ComunicadoDto): ComunicadoDto

    @DELETE("comunicados/{id}")
    suspend fun borrarComunicado(@Path("id") id: Long)

    // ---------- NOTAS ----------
    @GET("notas")
    suspend fun getNotas(): List<NotaDto>

    @POST("notas")
    suspend fun crearNota(@Body nota: NotaDto): NotaDto

    @PUT("notas/{id}")
    suspend fun actualizarNota(@Path("id") id: Long, @Body nota: NotaDto): NotaDto

    @DELETE("notas/{id}")
    suspend fun borrarNota(@Path("id") id: Long)

    // ---------- HIJOS ----------
    @GET("hijos")
    suspend fun getHijos(): List<HijoDto>

    @POST("hijos")
    suspend fun crearHijo(@Body hijo: HijoDto): HijoDto

    @PUT("hijos/{id}")
    suspend fun actualizarHijo(@Path("id") id: Long, @Body hijo: HijoDto): HijoDto

    @DELETE("hijos/{id}")
    suspend fun borrarHijo(@Path("id") id: Long)

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
