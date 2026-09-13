package com.example.myapplication.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Ayudante de la base de datos SQLite local.
 *
 * SQLiteOpenHelper crea/actualiza la base de datos del dispositivo.
 * Aquí definimos la tabla "contactos" (contactos de emergencia del apoderado).
 */
class ContactoDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "appescolar.db"
        private const val VERSION_BD = 1
        const val TABLA = "contactos"
    }

    // Se ejecuta la primera vez que se abre la base de datos.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLA (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "telefono TEXT)"
        )
    }

    // Se ejecuta al subir VERSION_BD (para migraciones). Aquí, recrea la tabla.
    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }
}
