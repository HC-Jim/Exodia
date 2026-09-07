package com.example.myapplication.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.domain.entities.Hijo
import com.example.myapplication.data.repositories.MockApoderado

/** Destinos principales del apoderado (máximo cuatro). */
enum class TabApoderado(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    SEGUIMIENTO("Seguimiento", Icons.Filled.LocationOn),
    COLEGIO("Colegio", Icons.Filled.School),
    PERFIL("Perfil", Icons.Filled.Person)
}

/** Pantallas que se abren por encima de una pestaña. */
enum class RutaApoderado {
    PERFIL_HIJO,
    NOTAS,
    ASISTENCIAS,
    EVENTO
}

class NavegadorApoderado(
    private val pila: SnapshotStateList<RutaApoderado>
) {
    var tab by mutableStateOf(TabApoderado.INICIO)
        private set

    var hijoActivo by mutableStateOf<Hijo>(MockApoderado.julio)
        private set

    val overlay: RutaApoderado? get() = pila.lastOrNull()

    fun seleccionarTab(nuevo: TabApoderado) {
        pila.clear()
        tab = nuevo
    }

    fun abrir(ruta: RutaApoderado, hijo: Hijo = hijoActivo) {
        hijoActivo = hijo
        pila.add(ruta)
    }

    fun retroceder(): Boolean {
        if (pila.isNotEmpty()) {
            pila.removeAt(pila.lastIndex)
            return true
        }
        return false
    }
}

@Composable
fun rememberNavegadorApoderado(): NavegadorApoderado {
    val pila = remember { mutableStateListOf<RutaApoderado>() }
    return remember { NavegadorApoderado(pila) }
}
