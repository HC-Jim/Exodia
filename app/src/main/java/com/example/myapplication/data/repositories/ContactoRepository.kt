package com.example.myapplication.data.repositories

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.data.local.ContactoDbHelper
import com.example.myapplication.domain.ContactoEmergencia

/**
 * Repositorio de contactos de emergencia (patron Repository sobre SQLite).
 *
 * El ViewModel habla SOLO con este repositorio; no sabe que por debajo hay
 * SQLite. Si manana se cambiara a Room o a una API, el ViewModel no cambiaria.
 */
class ContactoRepository(context: Context) {

    private val dbHelper = ContactoDbHelper(context)

    /** Devuelve los contactos de UN usuario (el mas nuevo primero). */
    fun listar(usuarioId: Long): List<ContactoEmergencia> {
        val lista = mutableListOf<ContactoEmergencia>()

        val db = dbHelper.readableDatabase
        val consulta = "SELECT id, nombre, telefono FROM ${ContactoDbHelper.TABLA} " +
            "WHERE usuario_id = ? ORDER BY id DESC"
        val cursor = db.rawQuery(consulta, arrayOf(usuarioId.toString()))

        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val nombre = cursor.getString(1)
            val telefono = cursor.getString(2) ?: ""

            val contacto = ContactoEmergencia(id = id, nombre = nombre, telefono = telefono)
            lista.add(contacto)
        }
        cursor.close()

        return lista
    }

    /** Inserta un contacto nuevo para ese usuario (CREATE). */
    fun agregar(usuarioId: Long, nombre: String, telefono: String) {
        val valores = ContentValues()
        valores.put("usuario_id", usuarioId)
        valores.put("nombre", nombre)
        valores.put("telefono", telefono)

        val db = dbHelper.writableDatabase
        db.insert(ContactoDbHelper.TABLA, null, valores)
    }

    /** Borra un contacto por su id (DELETE). */
    fun borrar(id: Long) {
        val db = dbHelper.writableDatabase
        val condicion = "id = ?"
        val argumentos = arrayOf(id.toString())
        db.delete(ContactoDbHelper.TABLA, condicion, argumentos)
    }
}
