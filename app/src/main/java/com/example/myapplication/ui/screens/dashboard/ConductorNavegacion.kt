package com.example.myapplication.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
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

/**
 * Destinos de la sección principal (máximo cuatro, sección 6.4 del documento).
 * La acción más frecuente del conductor —iniciar/seguir ruta— queda a un toque.
 */
enum class TabConductor(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    SEGUIMIENTO("Seguimiento", Icons.Filled.DirectionsBus),
    COLEGIO("Colegio", Icons.Filled.School),
    PERFIL("Perfil", Icons.Filled.Person)
}

/** Pantallas que se abren por encima de una pestaña (con retroceso). */
enum class RutaConductor {
    ESCANEAR_QR
}

/**
 * Navegador mínimo sin dependencias externas: una pestaña activa más una pila
 * de superposiciones. Suficiente para prototipar las interfaces; se puede
 * migrar a Navigation Compose cuando se conecten los ViewModel.
 */
class NavegadorConductor(
    tabInicial: TabConductor,
    private val pila: SnapshotStateList<RutaConductor>
) {
    var tab by mutableStateOf(tabInicial)
        private set

    val overlay: RutaConductor? get() = pila.lastOrNull()

    fun seleccionarTab(nuevo: TabConductor) {
        pila.clear()
        tab = nuevo
    }

    fun abrir(ruta: RutaConductor) {
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
fun rememberNavegadorConductor(
    tabInicial: TabConductor = TabConductor.INICIO
): NavegadorConductor {
    val pila = remember { mutableStateListOf<RutaConductor>() }
    return remember { NavegadorConductor(tabInicial, pila) }
}
