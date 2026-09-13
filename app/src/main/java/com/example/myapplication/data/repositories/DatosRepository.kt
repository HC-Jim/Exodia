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
 * Estrategia "primero la red, si falla la cache":
 *   1. Intenta pedir a la API (Retrofit).
 *   2. Si responde -> guarda la respuesta en SQLite (cache) y la devuelve.
 *   3. Si NO hay internet -> devuelve lo ultimo guardado en la cache.
 *
 * Ademas gestiona la cola de acciones pendientes y el historial de entregas.
 * Todo el trabajo pesado (red + SQLite) corre en Dispatchers.IO.
 */
class DatosRepository(context: Context) {

    private val api = RetrofitCliente.api
    private val db = OfflineDbHelper(context)

    private val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // ==========================================================
    //  COMUNICADOS (con cache)
    // ==========================================================
    suspend fun obtenerComunicados(): List<Comunicado> = withContext(Dispatchers.IO) {
        try {
            val dtos = api.getComunicados()   // pide a la API
            guardarComunicadosEnCache(dtos)   // guarda copia local
            dtos.aComunicados()
        } catch (e: Exception) {
            leerComunicadosDeCache().aComunicados()   // sin internet: cache
        }
    }

    private fun guardarComunicadosEnCache(lista: List<ComunicadoDto>) {
        val base = db.writableDatabase
        base.delete(OfflineDbHelper.T_COMUNICADOS, null, null)   // borra lo viejo

        for (c in lista) {
            val valores = ContentValues()
            valores.put("id", c.id)
            valores.put("titulo", c.titulo)
            valores.put("detalle", c.detalle)
            valores.put("fecha", c.fecha)
            base.insert(OfflineDbHelper.T_COMUNICADOS, null, valores)
        }
    }

    private fun leerComunicadosDeCache(): List<ComunicadoDto> {
        val lista = mutableListOf<ComunicadoDto>()

        val consulta = "SELECT id, titulo, detalle, fecha FROM ${OfflineDbHelper.T_COMUNICADOS} ORDER BY id"
        val cursor = db.readableDatabase.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            val dto = ComunicadoDto(
                id = cursor.getLong(0),
                titulo = cursor.getString(1),
                detalle = cursor.getString(2),
                fecha = cursor.getString(3)
            )
            lista.add(dto)
        }
        cursor.close()

        return lista
    }

    // ==========================================================
    //  NOTAS (con cache)
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
            val valores = ContentValues()
            valores.put("id", n.id)
            valores.put("curso", n.curso)
            valores.put("detalle", n.detalle)
            valores.put("valor", n.valor)
            base.insert(OfflineDbHelper.T_NOTAS, null, valores)
        }
    }

    private fun leerNotasDeCache(): List<NotaDto> {
        val lista = mutableListOf<NotaDto>()

        val consulta = "SELECT id, curso, detalle, valor FROM ${OfflineDbHelper.T_NOTAS} ORDER BY id"
        val cursor = db.readableDatabase.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            val dto = NotaDto(
                id = cursor.getLong(0),
                curso = cursor.getString(1),
                detalle = cursor.getString(2),
                valor = cursor.getString(3)
            )
            lista.add(dto)
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
    //  ALUMNOS (con cache + acciones pendientes aplicadas encima)
    // ==========================================================
    suspend fun obtenerAlumnos(): List<Alumno> = withContext(Dispatchers.IO) {
        // 1) Consigue los alumnos (de la API o, si no hay internet, de la cache).
        var dtos: List<AlumnoDto>
        try {
            dtos = api.getAlumnos()
            guardarAlumnosEnCache(dtos)
        } catch (e: Exception) {
            dtos = leerAlumnosDeCache()
        }

        // 2) Aplica los cambios de estado que aun no se han sincronizado.
        val pendientes = mapaPendientes()
        val lista = mutableListOf<Alumno>()
        for (dto in dtos) {
            val estadoPendiente = pendientes[dto.id]
            val dtoFinal = if (estadoPendiente != null) {
                dto.copy(estado = estadoPendiente)   // usa el estado que falta enviar
            } else {
                dto                                   // usa el estado tal cual vino
            }
            lista.add(dtoFinal.aDominio())
        }
        lista
    }

    private fun guardarAlumnosEnCache(lista: List<AlumnoDto>) {
        val base = db.writableDatabase
        base.delete(OfflineDbHelper.T_ALUMNOS, null, null)
        for (a in lista) {
            base.insert(OfflineDbHelper.T_ALUMNOS, null, valoresAlumno(a))
        }
    }

    private fun valoresAlumno(a: AlumnoDto): ContentValues {
        val valores = ContentValues()
        valores.put("id", a.id)
        valores.put("nombre", a.nombre)
        valores.put("grado", a.grado)
        valores.put("direccion", a.direccion)
        valores.put("paradero", a.paradero)
        valores.put("hora_entrega", a.horaEntrega)
        valores.put("estado", a.estado)
        valores.put("lat", a.lat)
        valores.put("lng", a.lng)
        return valores
    }

    private fun leerAlumnosDeCache(): List<AlumnoDto> {
        val lista = mutableListOf<AlumnoDto>()

        val consulta = "SELECT id, nombre, grado, direccion, paradero, hora_entrega, estado, lat, lng " +
            "FROM ${OfflineDbHelper.T_ALUMNOS} ORDER BY id"
        val cursor = db.readableDatabase.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            // lat/lng pueden venir vacios (null) en la base
            val lat = if (cursor.isNull(7)) null else cursor.getDouble(7)
            val lng = if (cursor.isNull(8)) null else cursor.getDouble(8)

            val dto = AlumnoDto(
                id = cursor.getLong(0),
                nombre = cursor.getString(1),
                grado = cursor.getString(2),
                direccion = cursor.getString(3),
                paradero = cursor.getString(4),
                horaEntrega = cursor.getString(5),
                estado = cursor.getString(6),
                lat = lat,
                lng = lng
            )
            lista.add(dto)
        }
        cursor.close()

        return lista
    }

    private fun leerAlumnoDeCache(id: Long): AlumnoDto? {
        val alumnos = leerAlumnosDeCache()
        for (a in alumnos) {
            if (a.id == id) return a
        }
        return null
    }

    // ==========================================================
    //  MARCAR ENTREGADO (offline-first) + HISTORIAL
    // ==========================================================
    /**
     * Marca un alumno como ENTREGADO:
     *   - actualiza la cache local (la UI lo ve al instante),
     *   - registra la entrega en el historial,
     *   - intenta avisar a la API; si no hay internet, lo deja EN COLA.
     */
    suspend fun marcarEntregado(alumno: Alumno) = withContext(Dispatchers.IO) {
        val id = alumno.id.toLongOrNull()
        if (id == null) {
            return@withContext
        }
        val nuevoEstado = "ENTREGADO"

        // 1) Actualiza la cache local.
        val cambio = ContentValues()
        cambio.put("estado", nuevoEstado)
        db.writableDatabase.update(OfflineDbHelper.T_ALUMNOS, cambio, "id = ?", arrayOf(id.toString()))

        // 2) Guarda la entrega en el historial.
        registrarEnHistorial(id, alumno.nombre, nuevoEstado)

        // 3) Intenta sincronizar ya; si falla, lo deja en la cola.
        try {
            val enCache = leerAlumnoDeCache(id)
            val dtoBase = enCache ?: alumnoDtoDesde(alumno)
            val dto = dtoBase.copy(estado = nuevoEstado)
            api.actualizarAlumno(id, dto)
        } catch (e: Exception) {
            encolarPendiente(id, nuevoEstado)
        }
        Unit
    }

    private fun alumnoDtoDesde(a: Alumno): AlumnoDto {
        val id = a.id.toLongOrNull() ?: 0
        return AlumnoDto(
            id = id,
            nombre = a.nombre,
            grado = a.grado,
            direccion = a.direccion,
            paradero = a.paradero,
            horaEntrega = a.horaEntrega,
            estado = a.estado.name
        )
    }

    private fun registrarEnHistorial(alumnoId: Long, nombre: String, estado: String) {
        val valores = ContentValues()
        valores.put("alumno_id", alumnoId)
        valores.put("alumno_nombre", nombre)
        valores.put("estado", estado)
        valores.put("fecha", formatoFecha.format(Date()))
        db.writableDatabase.insert(OfflineDbHelper.T_HISTORIAL, null, valores)
    }

    suspend fun obtenerHistorial(): List<RegistroHistorial> = withContext(Dispatchers.IO) {
        val lista = mutableListOf<RegistroHistorial>()

        val consulta = "SELECT id, alumno_nombre, estado, fecha FROM ${OfflineDbHelper.T_HISTORIAL} ORDER BY id DESC"
        val cursor = db.readableDatabase.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            val registro = RegistroHistorial(
                id = cursor.getLong(0),
                alumnoNombre = cursor.getString(1),
                estado = cursor.getString(2),
                fecha = cursor.getString(3)
            )
            lista.add(registro)
        }
        cursor.close()

        lista
    }

    // ==========================================================
    //  COLA DE ACCIONES PENDIENTES
    // ==========================================================
    private fun encolarPendiente(alumnoId: Long, nuevoEstado: String) {
        val valores = ContentValues()
        valores.put("alumno_id", alumnoId)
        valores.put("nuevo_estado", nuevoEstado)
        valores.put("creado", formatoFecha.format(Date()))
        db.writableDatabase.insert(OfflineDbHelper.T_PENDIENTES, null, valores)
    }

    /** Mapa alumno_id -> nuevo_estado con lo que falta sincronizar. */
    private fun mapaPendientes(): Map<Long, String> {
        val mapa = mutableMapOf<Long, String>()

        val consulta = "SELECT alumno_id, nuevo_estado FROM ${OfflineDbHelper.T_PENDIENTES}"
        val cursor = db.readableDatabase.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            val alumnoId = cursor.getLong(0)
            val estado = cursor.getString(1)
            mapa[alumnoId] = estado
        }
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
     * Envia a la API todas las acciones en cola. Cada una que se confirma se
     * borra de la cola. Devuelve cuantas se sincronizaron.
     */
    suspend fun sincronizar(): Int = withContext(Dispatchers.IO) {
        // Primero leemos toda la cola a listas simples (para no tener el cursor
        // abierto mientras hacemos las llamadas de red).
        val idsFila = mutableListOf<Long>()
        val idsAlumno = mutableListOf<Long>()
        val estados = mutableListOf<String>()

        val consulta = "SELECT id, alumno_id, nuevo_estado FROM ${OfflineDbHelper.T_PENDIENTES} ORDER BY id"
        val cursor = db.readableDatabase.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            idsFila.add(cursor.getLong(0))
            idsAlumno.add(cursor.getLong(1))
            estados.add(cursor.getString(2))
        }
        cursor.close()

        // Ahora intentamos enviar cada una.
        var enviadas = 0
        for (i in idsFila.indices) {
            val filaId = idsFila[i]
            val alumnoId = idsAlumno[i]
            val estado = estados[i]

            val enCache = leerAlumnoDeCache(alumnoId)
            if (enCache == null) {
                continue   // no tenemos datos del alumno; lo saltamos
            }

            try {
                val dto = enCache.copy(estado = estado)
                api.actualizarAlumno(alumnoId, dto)
                db.writableDatabase.delete(OfflineDbHelper.T_PENDIENTES, "id = ?", arrayOf(filaId.toString()))
                enviadas = enviadas + 1
            } catch (e: Exception) {
                // sigue sin internet: se queda en la cola para el proximo intento
            }
        }
        enviadas
    }

    // ==========================================================
    //  UBICACION DEL BUS (seguimiento)
    // ==========================================================
    /** Apoderado: lee la ultima posicion del bus (null si aun no hay). */
    suspend fun obtenerUbicacion(movilidad: String): Ubicacion? = withContext(Dispatchers.IO) {
        try {
            api.getUbicacion(movilidad).aDominio()
        } catch (e: Exception) {
            null
        }
    }

    /** Conductor: envia su posicion actual al servidor. */
    suspend fun enviarUbicacion(movilidad: String, lat: Double, lng: Double) = withContext(Dispatchers.IO) {
        try {
            val cuerpo = UbicacionDto(lat = lat, lng = lng)
            api.enviarUbicacion(movilidad, cuerpo)
        } catch (e: Exception) {
            // sin conexion: se reintenta en el proximo envio
        }
        Unit
    }
}
