package com.example.myapplication.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Base de datos SQLite de los recordatorios personales.
 * Es un CRUD 100% local (no depende de la API).
 */
class RecordatorioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "recordatorios.db"
        private const val VERSION_BD = 1
        const val TABLA = "recordatorios"
    }

    // Se ejecuta la primera vez que se abre la base de datos.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLA (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "detalle TEXT, " +
                "fecha TEXT)"
        )
    }

    // Se ejecuta al subir VERSION_BD (para migraciones). Aquí, recrea la tabla.
    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }
}
