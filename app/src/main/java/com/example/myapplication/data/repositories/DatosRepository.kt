package com.example.myapplication.data.repositories

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.data.local.OfflineDbHelper
import com.example.myapplication.data.models.AlumnoDto
import com.example.myapplication.data.models.ComunicadoDto
import com.example.myapplication.data.models.NotaDto
import com.example.myapplication.data.models.UbicacionDto
import com.example.myapplication.data.remote.aComunicados
import com.example.myapplication.data.remote.aDominio
import com.example.myapplication.data.remote.aHijos
import com.example.myapplication.data.remote.aNotas
import com.example.myapplication.domain.entities.Alumno
import com.example.myapplication.domain.entities.Comunicado
import com.example.myapplication.domain.entities.Hijo
import com.example.myapplication.domain.entities.Nota
import com.example.myapplication.domain.entities.RegistroHistorial
import com.example.myapplication.domain.entities.Ubicacion
import com.example.myapplication.services.api.RetrofitCliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio central de datos con soporte OFFLINE.
 *
 * Estrategia "primero la red, si falla la caché":
 *   1. Intenta pedir a la API (Retrofit).
 *   2. Si responde -> guarda la respuesta en SQLite (caché) y la devuelve.
 *   3. Si NO hay internet -> devuelve lo último guardado en la caché.
 *
 * Además gestiona la cola de acciones pendientes y el historial de entregas.
 * Todo el trabajo pesado (red + SQLite) corre en Dispatchers.IO.
 */
class DatosRepository(context: Context) {

    private val api = RetrofitCliente.api
    private val db = OfflineDbHelper(context)

    private val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // ==========================================================
    //  COMUNICADOS (con caché)
    // ==========================================================
    suspend fun obtenerComunicados(): List<Comunicado> = withContext(Dispatchers.IO) {
        try {
            val dtos = api.getComunicados()
            guardarComunicadosEnCache(dtos)
            dtos.aComunicados()
        } catch (e: Exception) {
            leerComunicadosDeCache().aComunicados()   // sin internet: caché
        }
    }

    private fun guardarComunicadosEnCache(lista: List<ComunicadoDto>) {
        val base = db.writableDatabase
        base.delete(OfflineDbHelper.T_COMUNICADOS, null, null)
        for (c in lista) {
            val v = ContentValues().apply {
                put("id", c.id); put("titulo", c.titulo)
                put("detalle", c.detalle); put("fecha", c.fecha)
            }
            base.insert(OfflineDbHelper.T_COMUNICADOS, null, v)
        }
    }

    private fun leerComunicadosDeCache(): List<ComunicadoDto> {
        val lista = mutableListOf<ComunicadoDto>()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, titulo, detalle, fecha FROM ${OfflineDbHelper.T_COMUNICADOS} ORDER BY id", null
        )
        while (cursor.moveToNext()) {
            lista.add(ComunicadoDto(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)))
        }
        cursor.close()
        return lista
    }

    // ==========================================================
    //  NOTAS (con caché)
    // ==========================================================
    suspend fun obtenerNotas(): List<Nota> = withContext(Dispatchers.IO) {
        try {
            val dtos = api.getNotas()
            guardarNotasEnCache(dtos)
            dtos.aNotas()
        } catch (e: Exception) {
            leerNotasDeCache().aNotas()
        }
    }

    private fun guardarNotasEnCache(lista: List<NotaDto>) {
        val base = db.writableDatabase
        base.delete(OfflineDbHelper.T_NOTAS, null, null)
        for (n in lista) {
            val v = ContentValues().apply {
                put("id", n.id); put("curso", n.curso)
                put("detalle", n.detalle); put("valor", n.valor)
            }
            base.insert(OfflineDbHelper.T_NOTAS, null, v)
        }
    }

    private fun leerNotasDeCache(): List<NotaDto> {
        val lista = mutableListOf<NotaDto>()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, curso, detalle, valor FROM ${OfflineDbHelper.T_NOTAS} ORDER BY id", null
        )
        while (cursor.moveToNext()) {
            lista.add(NotaDto(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)))
        }
        cursor.close()
        return lista
    }

    // ==========================================================
    //  HIJOS (solo red)
    // ==========================================================
    suspend fun obtenerHijos(): List<Hijo> = withContext(Dispatchers.IO) {
        api.getHijos().aHijos()
    }

    // ==========================================================
    //  UBICACIÓN DEL BUS (seguimiento)
    // ==========================================================
    /** Apoderado: lee la última posición del bus (null si aún no hay). */
    suspend fun obtenerUbicacion(movilidad: String): Ubicacion? = withContext(Dispatchers.IO) {
        try {
            api.getUbicacion(movilidad).aDominio()
        } catch (e: Exception) {
            null
        }
    }

    /** Conductor: envía su posición actual al servidor. */
    suspend fun enviarUbicacion(movilidad: String, lat: Double, lng: Double) =
        withContext(Dispatchers.IO) {
            try {
                api.enviarUbicacion(movilidad, UbicacionDto(lat = lat, lng = lng))
            } catch (e: Exception) {
                // sin conexión: se reintenta en el próximo envío
            }
            Unit
        }

    // ==========================================================
    //  ALUMNOS (con caché + acciones pendientes aplicadas encima)
    // ==========================================================
    suspend fun obtenerAlumnos(): List<Alumno> = withContext(Dispatchers.IO) {
        val dtos = try {
            val remoto = api.getAlumnos()
            guardarAlumnosEnCache(remoto)
            remoto
        } catch (e: Exception) {
            leerAlumnosDeCache()
        }
        // Aplica los cambios de estado que aún no se han sincronizado.
        val pendientes = mapaPendientes()
        dtos.map { dto ->
            val estado = pendientes[dto.id] ?: dto.estado
            dto.copy(estado = estado).aDominio()
        }
    }

    private fun guardarAlumnosEnCache(lista: List<AlumnoDto>) {
        val base = db.writableDatabase
        base.delete(OfflineDbHelper.T_ALUMNOS, null, null)
        for (a in lista) base.insert(OfflineDbHelper.T_ALUMNOS, null, valoresAlumno(a))
    }

    private fun valoresAlumno(a: AlumnoDto) = ContentValues().apply {
        put("id", a.id); put("nombre", a.nombre); put("grado", a.grado)
        put("direccion", a.direccion); put("paradero", a.paradero)
        put("hora_entrega", a.horaEntrega); put("estado", a.estado)
    }

    private fun leerAlumnosDeCache(): List<AlumnoDto> {
        val lista = mutableListOf<AlumnoDto>()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, nombre, grado, direccion, paradero, hora_entrega, estado " +
                "FROM ${OfflineDbHelper.T_ALUMNOS} ORDER BY id", null
        )
        while (cursor.moveToNext()) {
            lista.add(
                AlumnoDto(
                    id = cursor.getLong(0), nombre = cursor.getString(1), grado = cursor.getString(2),
                    direccion = cursor.getString(3), paradero = cursor.getString(4),
                    horaEntrega = cursor.getString(5), estado = cursor.getString(6)
                )
            )
        }
        cursor.close()
        return lista
    }

    private fun leerAlumnoDeCache(id: Long): AlumnoDto? =
        leerAlumnosDeCache().firstOrNull { it.id == id }

    // ==========================================================
    //  MARCAR ENTREGADO (offline-first) + HISTORIAL
    // ==========================================================
    /**
     * Marca un alumno como ENTREGADO:
     *   - actualiza la caché local (la UI lo ve al instante),
     *   - registra la entrega en el historial,
     *   - intenta avisar a la API; si no hay internet, lo deja EN COLA.
     */
    suspend fun marcarEntregado(alumno: Alumno) = withContext(Dispatchers.IO) {
        val id = alumno.id.toLongOrNull() ?: return@withContext
        val nuevoEstado = "ENTREGADO"

        // 1) Caché local
        db.writableDatabase.update(
            OfflineDbHelper.T_ALUMNOS,
            ContentValues().apply { put("estado", nuevoEstado) },
            "id = ?", arrayOf(id.toString())
        )
        // 2) Historial
        registrarEnHistorial(id, alumno.nombre, nuevoEstado)

        // 3) Intentar sincronizar ya; si falla, encolar
        try {
            val dto = (leerAlumnoDeCache(id) ?: alumnoDtoDesde(alumno)).copy(estado = nuevoEstado)
            api.actualizarAlumno(id, dto)
        } catch (e: Exception) {
            encolarPendiente(id, nuevoEstado)
        }
    }

    private fun alumnoDtoDesde(a: Alumno) = AlumnoDto(
        id = a.id.toLongOrNull() ?: 0,
        nombre = a.nombre, grado = a.grado, direccion = a.direccion,
        paradero = a.paradero, horaEntrega = a.horaEntrega, estado = a.estado.name
    )

    private fun registrarEnHistorial(alumnoId: Long, nombre: String, estado: String) {
        val v = ContentValues().apply {
            put("alumno_id", alumnoId); put("alumno_nombre", nombre)
            put("estado", estado); put("fecha", formatoFecha.format(Date()))
        }
        db.writableDatabase.insert(OfflineDbHelper.T_HISTORIAL, null, v)
    }

    suspend fun obtenerHistorial(): List<RegistroHistorial> = withContext(Dispatchers.IO) {
        val lista = mutableListOf<RegistroHistorial>()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, alumno_nombre, estado, fecha FROM ${OfflineDbHelper.T_HISTORIAL} ORDER BY id DESC", null
        )
        while (cursor.moveToNext()) {
            lista.add(RegistroHistorial(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)))
        }
        cursor.close()
        lista
    }

    // ==========================================================
    //  COLA DE ACCIONES PENDIENTES
    // ==========================================================
    private fun encolarPendiente(alumnoId: Long, nuevoEstado: String) {
        val v = ContentValues().apply {
            put("alumno_id", alumnoId); put("nuevo_estado", nuevoEstado)
            put("creado", formatoFecha.format(Date()))
        }
        db.writableDatabase.insert(OfflineDbHelper.T_PENDIENTES, null, v)
    }

    /** Mapa alumno_id -> nuevo_estado con lo que falta sincronizar. */
    private fun mapaPendientes(): Map<Long, String> {
        val mapa = mutableMapOf<Long, String>()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT alumno_id, nuevo_estado FROM ${OfflineDbHelper.T_PENDIENTES}", null
        )
        while (cursor.moveToNext()) mapa[cursor.getLong(0)] = cursor.getString(1)
        cursor.close()
        return mapa
    }

    suspend fun contarPendientes(): Int = withContext(Dispatchers.IO) {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM ${OfflineDbHelper.T_PENDIENTES}", null
        )
        cursor.moveToFirst()
        val total = cursor.getInt(0)
        cursor.close()
        total
    }

    /**
     * Envía a la API todas las acciones en cola. Cada una que se confirma se
     * borra de la cola. Devuelve cuántas se sincronizaron.
     */
    suspend fun sincronizar(): Int = withContext(Dispatchers.IO) {
        var enviadas = 0
        val cursor = db.readableDatabase.rawQuery(
            "SELECT id, alumno_id, nuevo_estado FROM ${OfflineDbHelper.T_PENDIENTES} ORDER BY id", null
        )
        // Copiamos primero para no mantener el cursor abierto durante la red.
        data class Pend(val filaId: Long, val alumnoId: Long, val estado: String)
        val pendientes = mutableListOf<Pend>()
        while (cursor.moveToNext()) {
            pendientes.add(Pend(cursor.getLong(0), cursor.getLong(1), cursor.getString(2)))
        }
        cursor.close()

        for (p in pendientes) {
            try {
                val dto = (leerAlumnoDeCache(p.alumnoId) ?: continue).copy(estado = p.estado)
                api.actualizarAlumno(p.alumnoId, dto)
                db.writableDatabase.delete(OfflineDbHelper.T_PENDIENTES, "id = ?", arrayOf(p.filaId.toString()))
                enviadas++
            } catch (e: Exception) {
                // Sigue sin internet: se queda en la cola para el próximo intento.
            }
        }
        enviadas
    }
}
