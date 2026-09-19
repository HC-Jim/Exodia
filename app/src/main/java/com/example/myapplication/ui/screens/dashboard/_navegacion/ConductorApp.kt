package com.example.myapplication.ui.screens.dashboard._navegacion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.ui.components.BarraInferiorConductor
import com.example.myapplication.ui.screens.dashboard.alumnos.AlumnosEntregadosScreen
import com.example.myapplication.ui.screens.dashboard.ubicacion.RutaActivaScreen
import com.example.myapplication.ui.screens.profile.AjustesScreen
import com.example.myapplication.ui.screens.profile.PerfilConductorScreen
import com.example.myapplication.ui.screens.secure.EscanearQRScreen

// ============================================================
//  Navegación del conductor (pestañas + pila de overlays)
// ============================================================

/** Pestañas de la barra inferior del conductor. */
enum class TabConductor(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    SEGUIMIENTO("Seguimiento", Icons.Filled.DirectionsBus),
    ESTUDIANTES("Estudiantes", Icons.Filled.Groups),
    PERFIL("Perfil", Icons.Filled.Person),
    AJUSTES("Ajustes", Icons.Filled.Settings)
}

/** Pantallas que se abren por encima de una pestaña (con retroceso). */
enum class RutaConductor {
    ESCANEAR_QR
}

/**
 * Navegador mínimo sin dependencias externas: una pestaña activa más una pila
 * de superposiciones (la última es la que se ve).
 */
class NavegadorConductor(
    tabInicial: TabConductor,
    private val pila: SnapshotStateList<RutaConductor>
) {
    var tab by mutableStateOf(tabInicial)
        private set

    // Pantalla encima de la pestaña, o null si se ve la pestaña normal.
    val overlay: RutaConductor? get() = pila.lastOrNull()

    // Cambia de pestaña y cierra cualquier overlay abierto.
    fun seleccionarTab(nuevo: TabConductor) {
        pila.clear()
        tab = nuevo
    }

    // Abre una pantalla encima de la pestaña actual.
    fun abrir(ruta: RutaConductor) {
        pila.add(ruta)
    }

    // Cierra el último overlay. Devuelve true si había algo que cerrar.
    fun retroceder(): Boolean {
        if (pila.isNotEmpty()) {
            pila.removeAt(pila.lastIndex)
            return true
        }
        return false
    }
}

// Crea y recuerda el navegador para que sobreviva a las recomposiciones.
@Composable
fun rememberNavegadorConductor(
    tabInicial: TabConductor = TabConductor.INICIO
): NavegadorConductor {
    val pila = remember { mutableStateListOf<RutaConductor>() }
    return remember { NavegadorConductor(tabInicial, pila) }
}

// ============================================================
//  Pantalla contenedora del conductor
// ============================================================

/**
 * Contenedor del rol Conductor: barra inferior con pestañas (incluida la de
 * Estudiantes) y una superposición para el escáner QR.
 */
@Composable
fun ConductorApp(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {
    val nav = rememberNavegadorConductor()
    val overlay = nav.overlay

    // Si hay un overlay abierto, el botón Atrás del sistema lo cierra en vez de salir de la app.
    BackHandler(enabled = overlay != null) { nav.retroceder() }

    if (overlay != null) {
        // Superposiciones a pantalla completa, con su propio botón de retroceso.
        Box(modifier.fillMaxSize()) {
            when (overlay) {
                RutaConductor.ESCANEAR_QR -> EscanearQRScreen(onRetroceder = { nav.retroceder() })
            }
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BarraInferiorConductor(nav) }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            // Muestra la pantalla de la pestaña activa.
            when (nav.tab) {
                TabConductor.INICIO -> InicioConductorScreen(
                    onIniciarRuta = { nav.seleccionarTab(TabConductor.SEGUIMIENTO) },
                    onFinalizarRuta = { nav.seleccionarTab(TabConductor.PERFIL) }
                )

                TabConductor.SEGUIMIENTO -> RutaActivaScreen(
                    onEscanearQR = { nav.abrir(RutaConductor.ESCANEAR_QR) }
                )

                // Pestaña de estudiantes: lista completa con el estado de cada uno.
                TabConductor.ESTUDIANTES -> AlumnosEntregadosScreen()

                TabConductor.PERFIL -> PerfilConductorScreen(onCerrarSesion = onCerrarSesion)

                TabConductor.AJUSTES -> AjustesScreen(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}
