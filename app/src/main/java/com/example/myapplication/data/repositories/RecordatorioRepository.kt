package com.example.myapplication.data.repositories

import android.content.ContentValues
import android.content.Context
import com.example.myapplication.data.local.RecordatorioDbHelper
import com.example.myapplication.domain.Recordatorio

/**
 * Repositorio de recordatorios personales (patrón Repository sobre SQLite).
 * El ViewModel solo habla con este repositorio.
 */
class RecordatorioRepository(context: Context) {

    private val dbHelper = RecordatorioDbHelper(context)

    /** Lista todos los recordatorios (el más nuevo primero). */
    fun listar(): List<Recordatorio> {
        val lista = mutableListOf<Recordatorio>()

        val db = dbHelper.readableDatabase
        val consulta = "SELECT id, titulo, detalle, fecha FROM ${RecordatorioDbHelper.TABLA} ORDER BY id DESC"
        val cursor = db.rawQuery(consulta, null)
        while (cursor.moveToNext()) {
            val recordatorio = Recordatorio(
                id = cursor.getLong(0),
                titulo = cursor.getString(1),
                detalle = cursor.getString(2) ?: "",
                fecha = cursor.getString(3) ?: ""
            )
            lista.add(recordatorio)
        }
        cursor.close()

        return lista
    }

    /** Inserta un recordatorio nuevo (CREATE). */
    fun agregar(titulo: String, detalle: String, fecha: String) {
        val valores = ContentValues()
        valores.put("titulo", titulo)
        valores.put("detalle", detalle)
        valores.put("fecha", fecha)

        val db = dbHelper.writableDatabase
        db.insert(RecordatorioDbHelper.TABLA, null, valores)
    }

    /** Borra un recordatorio por su id (DELETE). */
    fun borrar(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(RecordatorioDbHelper.TABLA, "id = ?", arrayOf(id.toString()))
    }
}
