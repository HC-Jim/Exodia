package com.example.myapplication.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Base de datos SQLite para el soporte OFFLINE de la app. Contiene:
 *
 *  1) Caché de la API: guarda la última respuesta de comunicados, notas y
 *     alumnos para poder mostrarlos sin internet.
 *  2) Cola de acciones pendientes: cambios hechos sin señal (ej. marcar un
 *     alumno como ENTREGADO) que se enviarán a la API cuando vuelva la conexión.
 *  3) Historial de entregas: registro local de cada entrega realizada.
 */
class OfflineDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "appescolar_offline.db"
        private const val VERSION_BD = 1

        const val T_COMUNICADOS = "cache_comunicados"
        const val T_NOTAS = "cache_notas"
        const val T_ALUMNOS = "cache_alumnos"
        const val T_PENDIENTES = "acciones_pendientes"
        const val T_HISTORIAL = "historial_entregas"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // --- Cachés (guardan el id real del servidor) ---
        db.execSQL(
            "CREATE TABLE $T_COMUNICADOS (" +
                "id INTEGER PRIMARY KEY, titulo TEXT, detalle TEXT, fecha TEXT)"
        )
        db.execSQL(
            "CREATE TABLE $T_NOTAS (" +
                "id INTEGER PRIMARY KEY, curso TEXT, detalle TEXT, valor TEXT)"
        )
        db.execSQL(
            "CREATE TABLE $T_ALUMNOS (" +
                "id INTEGER PRIMARY KEY, nombre TEXT, grado TEXT, direccion TEXT, " +
                "paradero TEXT, hora_entrega TEXT, estado TEXT)"
        )
        // --- Cola de acciones pendientes de sincronizar ---
        db.execSQL(
            "CREATE TABLE $T_PENDIENTES (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, alumno_id INTEGER, " +
                "nuevo_estado TEXT, creado TEXT)"
        )
        // --- Historial local de entregas ---
        db.execSQL(
            "CREATE TABLE $T_HISTORIAL (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, alumno_id INTEGER, " +
                "alumno_nombre TEXT, estado TEXT, fecha TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS $T_COMUNICADOS")
        db.execSQL("DROP TABLE IF EXISTS $T_NOTAS")
        db.execSQL("DROP TABLE IF EXISTS $T_ALUMNOS")
        db.execSQL("DROP TABLE IF EXISTS $T_PENDIENTES")
        db.execSQL("DROP TABLE IF EXISTS $T_HISTORIAL")
        onCreate(db)
    }
}
