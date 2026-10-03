package com.example.myapplication.ui.screens.profile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.repositories.ContactoRepository
import com.example.myapplication.domain.ContactoEmergencia
import com.example.myapplication.utils.Sesion

/**
 * ViewModel de contactos de emergencia (SQLite vía ContactoRepository).
 *
 * Mantiene la lista en un estado observable de Compose. Cada acción
 * (agregar / borrar) escribe en SQLite y recarga la lista desde la base.
 */
class ContactosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ContactoRepository(app)

    var contactos by mutableStateOf<List<ContactoEmergencia>>(emptyList())
        private set

    // Id del usuario en sesión: sus contactos son privados (no se comparten).
    private val usuarioId: Long
        get() = Sesion.usuario?.id ?: -1L

    init {
        cargar()
    }

    fun cargar() {
        contactos = repo.listar(usuarioId)
    }

    fun agregar(nombre: String, telefono: String) {
        if (nombre.isBlank()) return          // no guardamos contactos sin nombre
        repo.agregar(usuarioId, nombre.trim(), telefono.trim())
        cargar()                              // releer para reflejar el cambio
    }

    fun borrar(id: Long) {
        repo.borrar(id)
        cargar()
    }
}
