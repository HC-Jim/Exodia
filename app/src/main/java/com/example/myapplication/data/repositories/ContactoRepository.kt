package com.example.myapplication.data.repositories

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.data.local.ContactoDbHelper
import com.example.myapplication.domain.entities.ContactoEmergencia

/**
 * Repositorio de contactos de emergencia (patrón Repository sobre SQLite).
 *
 * El ViewModel habla SOLO con este repositorio; no sabe que por debajo hay
 * SQLite. Si mañana se cambiara a Room o a una API, el ViewModel no cambiaría.
 */
class ContactoRepository(context: Context) {

    private val dbHelper = ContactoDbHelper(context)

    /** Devuelve todos los contactos guardados (el más nuevo primero). */
    fun listar(): List<ContactoEmergencia> {
        val lista = mutableListOf<ContactoEmergencia>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, telefono FROM ${ContactoDbHelper.TABLA} ORDER BY id DESC",
            null
        )
        while (cursor.moveToNext()) {
            lista.add(
                ContactoEmergencia(
                    id = cursor.getLong(0),
                    nombre = cursor.getString(1),
                    telefono = cursor.getString(2) ?: ""
                )
            )
        }
        cursor.close()
        return lista
    }

    /** Inserta un contacto nuevo (CREATE). */
    fun agregar(nombre: String, telefono: String) {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("telefono", telefono)
        }
        db.insert(ContactoDbHelper.TABLA, null, valores)
    }

    /** Borra un contacto por su id (DELETE). */
    fun borrar(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(ContactoDbHelper.TABLA, "id = ?", arrayOf(id.toString()))
    }
}
