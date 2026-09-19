package com.example.myapplication.ui.screens.dashboard.recordatorios

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.repositories.RecordatorioRepository
import com.example.myapplication.domain.entities.Recordatorio

/**
 * ViewModel de los recordatorios personales (SQLite vía RecordatorioRepository).
 */
class RecordatoriosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RecordatorioRepository(app)

    // Lista de recordatorios que muestra la pantalla.
    var recordatorios by mutableStateOf<List<Recordatorio>>(emptyList())
        private set

    init {
        cargar()
    }

    // Relee la lista desde SQLite (se llama tras agregar o borrar para refrescar la UI).
    fun cargar() {
        recordatorios = repo.listar()
    }

    fun agregar(titulo: String, detalle: String, fecha: String) {
        if (titulo.isBlank()) return          // el título es obligatorio
        repo.agregar(titulo.trim(), detalle.trim(), fecha.trim())
        cargar()
    }

    fun borrar(id: Long) {
        repo.borrar(id)
        cargar()
    }
}
