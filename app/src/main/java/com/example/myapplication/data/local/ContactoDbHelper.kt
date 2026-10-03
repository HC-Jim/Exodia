package com.example.myapplication.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
crea la tabla contactos
 */
class ContactoDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "appescolar.db"
        private const val VERSION_BD = 2
        const val TABLA = "contactos"
    }

    // Se ejecuta la primera vez que se abre la base de datos.
    // Cada contacto pertenece a un usuario (usuario_id), para que sean privados.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLA (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER NOT NULL, " +
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
