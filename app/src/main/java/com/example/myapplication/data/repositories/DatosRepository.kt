package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.models.EstadoBody
import com.example.myapplication.data.models.UbicacionDto
import com.example.myapplication.data.remote.aAlumnos
import com.example.myapplication.data.remote.aComunicados
import com.example.myapplication.data.remote.aDominio
import com.example.myapplication.data.remote.aNotas
import com.example.myapplication.data.remote.estadoAsistenciaDe
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.Comunicado
import com.example.myapplication.domain.entities.EstadoAsistencia
import com.example.myapplication.domain.entities.Nota
import com.example.myapplication.domain.entities.Ubicacion
import com.example.myapplication.services.api.RetrofitCliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio central de datos. Lee y escribe DIRECTO en la API (Retrofit).
 * No usa caché local: SQLite solo se usa para contactos de emergencia y
 * recordatorios (en sus propios repositorios).
 */
class DatosRepository(context: Context) {

    private val api = RetrofitCliente.api

    // ==========================================================
    //  Lecturas del rol Apoderado/Estudiante
    // ==========================================================
    suspend fun obtenerComunicados(): List<Comunicado> = withContext(Dispatchers.IO) {
        api.getComunicados().aComunicados()
    }

    suspend fun obtenerNotas(): List<Nota> = withContext(Dispatchers.IO) {
        api.getNotas().aNotas()
    }

    // ==========================================================
    //  Estudiantes de una movilidad (rol Conductor)
    // ==========================================================
    /** Lista de estudiantes que el conductor debe recoger. */
    suspend fun obtenerEstudiantes(movilidad: String): List<Alumno> = withContext(Dispatchers.IO) {
        try {
            api.getEstudiantes(movilidad).aAlumnos()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Marca al estudiante como ENTREGADO (directo en la API). */
    suspend fun marcarEntregado(alumno: Alumno) = withContext(Dispatchers.IO) {
        cambiarEstado(alumno, "ENTREGADO")
        Unit
    }

    /** Cancela el recojo del estudiante (directo en la API). */
    suspend fun cancelar(alumno: Alumno, motivo: String) = withContext(Dispatchers.IO) {
        cambiarEstado(alumno, "CANCELADO")
        Unit
    }

    private suspend fun cambiarEstado(alumno: Alumno, estado: String) {
        val id = alumno.id.toLongOrNull() ?: return
        try {
            api.actualizarEstadoUsuario(id, EstadoBody(estado))
        } catch (e: Exception) {
            // sin conexión: no se guarda (ya no hay cola local)
        }
    }

    /** Estado de recojo de un estudiante (para que él mismo lo vea). */
    suspend fun obtenerEstadoEstudiante(id: Long): String? = withContext(Dispatchers.IO) {
        try {
            api.getUsuario(id).estado
        } catch (e: Exception) {
            null
        }
    }

    /** Reinicia la ruta: todos los estudiantes de la movilidad vuelven a PENDIENTE. */
    suspend fun reiniciarRuta(movilidad: String) = withContext(Dispatchers.IO) {
        try {
            val estudiantes = api.getEstudiantes(movilidad)
            for (e in estudiantes) {
                api.actualizarEstadoUsuario(e.id, EstadoBody("PENDIENTE"))
            }
        } catch (e: Exception) {
            // sin conexión: no se reinicia
        }
        Unit
    }

    // ==========================================================
    //  Asistencias
    // ==========================================================
    suspend fun obtenerAsistencias(usuarioId: Long): Map<Int, EstadoAsistencia> = withContext(Dispatchers.IO) {
        try {
            val dtos = api.getAsistencias(usuarioId)
            val mapa = mutableMapOf<Int, EstadoAsistencia>()
            for (d in dtos) {
                mapa[d.dia] = estadoAsistenciaDe(d.estado)
            }
            mapa
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // ==========================================================
    //  Ubicación del bus (seguimiento)
    // ==========================================================
    suspend fun obtenerUbicacion(movilidad: String): Ubicacion? = withContext(Dispatchers.IO) {
        try {
            api.getUbicacion(movilidad).aDominio()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun enviarUbicacion(movilidad: String, lat: Double, lng: Double) = withContext(Dispatchers.IO) {
        try {
            api.enviarUbicacion(movilidad, UbicacionDto(lat = lat, lng = lng))
        } catch (e: Exception) {
            // sin conexión: se reintenta en el próximo envío
        }
        Unit
    }
}
