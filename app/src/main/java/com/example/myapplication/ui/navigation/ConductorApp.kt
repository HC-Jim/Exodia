package com.example.myapplication.ui.navigation

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.ui.components.BarraInferiorConductor
import com.example.myapplication.ui.screens.alumnos.AlumnosEntregadosScreen
import com.example.myapplication.ui.screens.ubicacion.RutaActivaScreen
import com.example.myapplication.ui.screens.profile.AjustesScreen
import com.example.myapplication.ui.screens.profile.PerfilConductorScreen

// ============================================================
//  Navegación del conductor (solo pestañas)
// ============================================================

/** Pestañas de la barra inferior del conductor. */
enum class TabConductor(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    SEGUIMIENTO("Seguimiento", Icons.Filled.DirectionsBus),
    ESTUDIANTES("Estudiantes", Icons.Filled.Groups),
    PERFIL("Perfil", Icons.Filled.Person),
    AJUSTES("Ajustes", Icons.Filled.Settings)
}

/** Navegador mínimo del conductor: solo recuerda la pestaña activa. */
class NavegadorConductor(tabInicial: TabConductor) {
    var tab by mutableStateOf(tabInicial)
        private set

    fun seleccionarTab(nuevo: TabConductor) {
        tab = nuevo
    }
}

// Crea y recuerda el navegador para que sobreviva a las recomposiciones.
@Composable
fun rememberNavegadorConductor(
    tabInicial: TabConductor = TabConductor.INICIO
): NavegadorConductor {
    return remember { NavegadorConductor(tabInicial) }
}

// ============================================================
//  Pantalla contenedora del conductor
// ============================================================

/**
 * Contenedor del rol Conductor: barra inferior con pestañas
 * (Inicio, Seguimiento, Estudiantes, Perfil, Ajustes).
 */
@Composable
fun ConductorApp(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {
    val nav = rememberNavegadorConductor()

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

                TabConductor.SEGUIMIENTO -> RutaActivaScreen()

                // Pestaña de estudiantes: lista completa con el estado de cada uno.
                TabConductor.ESTUDIANTES -> AlumnosEntregadosScreen()

                TabConductor.PERFIL -> PerfilConductorScreen(onCerrarSesion = onCerrarSesion)

                TabConductor.AJUSTES -> AjustesScreen(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}
