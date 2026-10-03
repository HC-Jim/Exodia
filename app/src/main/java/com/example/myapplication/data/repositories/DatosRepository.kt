package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.models.EstadoBody
import com.example.myapplication.data.models.PerfilBody
import com.example.myapplication.domain.Alumno
import com.example.myapplication.domain.Usuario
import com.example.myapplication.domain.CalendarioAsistencia
import com.example.myapplication.domain.Comunicado
import com.example.myapplication.domain.Nota
import com.example.myapplication.domain.Ubicacion
import com.example.myapplication.data.remote.RetrofitCliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio central de datos. Lee y escribe DIRECTO en la API (Retrofit).
 */
class DatosRepository(context: Context) {

    private val api = RetrofitCliente.api

    // ==========================================================
    //  Lecturas del rol Apoderado/Estudiante
    // ==========================================================
    suspend fun obtenerComunicados(): List<Comunicado> = withContext(Dispatchers.IO) {
        api.getComunicados()   // directo del servidor (Gson lo llena)
    }

    suspend fun obtenerNotas(): List<Nota> = withContext(Dispatchers.IO) {
        api.getNotas()         // directo del servidor
    }

    /** Actualiza los datos personales del perfil y devuelve el usuario actualizado. */
    suspend fun actualizarPerfil(id: Long, body: PerfilBody): Usuario? = withContext(Dispatchers.IO) {
        try {
            api.actualizarUsuario(id, body)
        } catch (e: Exception) {
            null
        }
    }

    // ==========================================================
    //  Estudiantes de una movilidad (rol Conductor)
    // ==========================================================
    /** Lista de estudiantes que el conductor debe recoger. */
    suspend fun obtenerEstudiantes(movilidad: String): List<Alumno> = withContext(Dispatchers.IO) {
        try {
            api.getEstudiantes(movilidad)   // el servidor ya devuelve la forma "alumno"
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

    /** Reinicia la ruta: el servidor pone a todos los estudiantes en PENDIENTE (una sola llamada). */
    suspend fun reiniciarRuta(movilidad: String) = withContext(Dispatchers.IO) {
        try {
            api.reiniciarRuta(movilidad)
        } catch (e: Exception) {
            // sin conexión: no se reinicia
        }
        Unit
    }

    // ==========================================================
    //  Asistencias
    // ==========================================================
    /** Calendario de asistencia del mes (lo arma el servidor; la app lo consume directo). */
    suspend fun obtenerAsistencias(usuarioId: Long): CalendarioAsistencia? = withContext(Dispatchers.IO) {
        try {
            api.getAsistencias(usuarioId)
        } catch (e: Exception) {
            null
        }
    }

    // ==========================================================
    //  Ubicación del bus (seguimiento)
    // ==========================================================
    suspend fun obtenerUbicacion(movilidad: String): Ubicacion? = withContext(Dispatchers.IO) {
        try {
            api.getUbicacion(movilidad)   // directo del servidor
        } catch (e: Exception) {
            null
        }
    }

    suspend fun enviarUbicacion(movilidad: String, lat: Double, lng: Double) = withContext(Dispatchers.IO) {
        try {
            api.enviarUbicacion(movilidad, Ubicacion(lat = lat, lng = lng))
        } catch (e: Exception) {
            // sin conexión: se reintenta en el próximo envío
        }
        Unit
    }
}
