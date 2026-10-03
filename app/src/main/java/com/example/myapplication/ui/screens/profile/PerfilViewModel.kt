package com.example.myapplication.ui.screens.profile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.PerfilBody
import com.example.myapplication.data.repositories.DatosRepository
import com.example.myapplication.utils.Sesion
import kotlinx.coroutines.launch

/**
 * ViewModel del perfil (datos personales en la nube, tabla usuarios).
 * Carga los campos del usuario en sesión y los guarda con "Guardar cambios".
 */
class PerfilViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = DatosRepository(app)

    // Campos editables (inicializados con lo que hay en la sesión).
    var nombre by mutableStateOf(Sesion.usuario?.nombre ?: "")
    var celular by mutableStateOf(Sesion.usuario?.celular ?: "")
    var correo by mutableStateOf(Sesion.usuario?.correo ?: "")
    var contrasena by mutableStateOf(Sesion.usuario?.contrasena ?: "")

    var guardando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)

    /** Reponer los valores desde la sesión (botón Cancelar). */
    fun cancelar() {
        nombre = Sesion.usuario?.nombre ?: ""
        celular = Sesion.usuario?.celular ?: ""
        correo = Sesion.usuario?.correo ?: ""
        contrasena = Sesion.usuario?.contrasena ?: ""
        mensaje = null
    }

    /**
     * Guarda los cambios en la nube. [incluirCorreo] = true para el conductor
     * (también edita su correo). La contraseña se envía siempre que no esté vacía.
     */
    fun guardar(incluirCorreo: Boolean) {
        val id = Sesion.usuario?.id ?: return
        viewModelScope.launch {
            guardando = true
            mensaje = null
            val body = PerfilBody(
                nombre = nombre,
                celular = celular,
                correo = if (incluirCorreo) correo else null,
                contrasena = if (contrasena.isNotBlank()) contrasena else null
            )
            val actualizado = repo.actualizarPerfil(id, body)
            if (actualizado != null) {
                // El servidor no devuelve la contraseña nueva; la conservamos localmente.
                Sesion.usuario = actualizado.copy(contrasena = contrasena)
                mensaje = "Datos actualizados"
            } else {
                mensaje = "No se pudo actualizar"
            }
            guardando = false
        }
    }
}
