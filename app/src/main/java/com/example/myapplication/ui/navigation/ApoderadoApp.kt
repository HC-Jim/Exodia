package com.example.myapplication.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.domain.Comunicado
import com.example.myapplication.ui.components.BarraInferiorApoderado
import com.example.myapplication.ui.screens.asistencias.AsistenciasScreen
import com.example.myapplication.ui.screens.comunicados.ComunicadosScreen
import com.example.myapplication.ui.screens.comunicados.EventoDetalleScreen
import com.example.myapplication.ui.screens.notas.NotasScreen
import com.example.myapplication.ui.screens.recordatorios.RecordatoriosScreen
import com.example.myapplication.ui.screens.ubicacion.SeguimientoApoderadoScreen
import com.example.myapplication.ui.screens.profile.AjustesScreen
import com.example.myapplication.ui.screens.profile.ConfiguracionApoderadoScreen
import com.example.myapplication.ui.screens.profile.PerfilAlumnoScreen

// ============================================================
//  Navegación del apoderado (pestañas + pila de overlays)
// ============================================================

/** Pestañas de la barra inferior del apoderado. */
enum class TabApoderado(val etiqueta: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    SEGUIMIENTO("Seguimiento", Icons.Filled.LocationOn),
    COLEGIO("Colegio", Icons.Filled.School),
    PERFIL("Perfil", Icons.Filled.Person),
    AJUSTES("Ajustes", Icons.Filled.Settings)
}

/** Pantallas que se abren por encima de una pestaña. */
enum class RutaApoderado {
    PERFIL_HIJO,
    NOTAS,
    ASISTENCIAS,
    EVENTO,
    RECORDATORIOS
}

/**
 * Controla la navegación del apoderado: la pestaña actual y una pila de pantallas
 * abiertas encima (overlays). Es un router hecho a mano, sin Navigation-Compose.
 */
class NavegadorApoderado(
    private val pila: SnapshotStateList<RutaApoderado>   // pila de overlays (la última es la visible)
) {
    var tab by mutableStateOf(TabApoderado.INICIO)
        private set

    // Pantalla que está encima de la pestaña, o null si se ve la pestaña normal.
    val overlay: RutaApoderado? get() = pila.lastOrNull()

    // Cambia de pestaña y cierra cualquier overlay abierto.
    fun seleccionarTab(nuevo: TabApoderado) {
        pila.clear()
        tab = nuevo
    }

    // Abre una pantalla encima de la pestaña actual.
    fun abrir(ruta: RutaApoderado) {
        pila.add(ruta)
    }

    // Cierra el último overlay. Devuelve true si había algo que cerrar (para el botón Atrás).
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
fun rememberNavegadorApoderado(): NavegadorApoderado {
    val pila = remember { mutableStateListOf<RutaApoderado>() }
    return remember { NavegadorApoderado(pila) }
}

// ============================================================
//  Pantalla contenedora del apoderado
// ============================================================

/**
 * Contenedor del rol Apoderado: barra inferior con pestañas y pantallas que se
 * abren encima (perfil del hijo, notas, asistencias, evento, recordatorios).
 */
@Composable
fun ApoderadoApp(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {
    val nav = rememberNavegadorApoderado()
    val overlay = nav.overlay

    // Comunicado que se tocó en la lista; lo usa la pantalla de detalle.
    var comunicadoSel by remember { mutableStateOf<Comunicado?>(null) }

    // Con un overlay abierto, el botón Atrás lo cierra en lugar de salir de la app.
    BackHandler(enabled = overlay != null) { nav.retroceder() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (overlay == null) BarraInferiorApoderado(nav.tab) { nav.seleccionarTab(it) }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            // Si hay overlay se muestra ese; con null (sin overlay) se muestra la pestaña activa.
            when (overlay) {
                RutaApoderado.PERFIL_HIJO -> PerfilAlumnoScreen(
                    onRetroceder = { nav.retroceder() },
                    onNotas = { nav.abrir(RutaApoderado.NOTAS) },
                    onAsistencias = { nav.abrir(RutaApoderado.ASISTENCIAS) },
                    onCerrarSesion = onCerrarSesion
                )

                RutaApoderado.NOTAS -> NotasScreen(onRetroceder = { nav.retroceder() })

                RutaApoderado.ASISTENCIAS -> AsistenciasScreen(onRetroceder = { nav.retroceder() })

                RutaApoderado.EVENTO -> {
                    val com = comunicadoSel
                    if (com != null) {
                        EventoDetalleScreen(comunicado = com, onRetroceder = { nav.retroceder() })
                    } else {
                        // Sin comunicado elegido (no debería pasar): vuelve a la lista.
                        LaunchedEffect(Unit) { nav.retroceder() }
                    }
                }

                RutaApoderado.RECORDATORIOS -> RecordatoriosScreen(onRetroceder = { nav.retroceder() })

                null -> when (nav.tab) {
                    TabApoderado.INICIO -> InicioApoderadoScreen(
                        onColegio = { nav.seleccionarTab(TabApoderado.COLEGIO) },
                        onCalendario = { nav.seleccionarTab(TabApoderado.COLEGIO) },
                        onAsistencias = { nav.abrir(RutaApoderado.ASISTENCIAS) },
                        onNotas = { nav.abrir(RutaApoderado.NOTAS) },
                        onEventos = { nav.seleccionarTab(TabApoderado.COLEGIO) },   // eventos = comunicados
                        onRecordatorios = { nav.abrir(RutaApoderado.RECORDATORIOS) }
                    )

                    TabApoderado.SEGUIMIENTO -> SeguimientoApoderadoScreen()

                    TabApoderado.COLEGIO -> ComunicadosScreen(
                        onComunicadoClick = { com ->
                            comunicadoSel = com
                            nav.abrir(RutaApoderado.EVENTO)
                        }
                    )

                    TabApoderado.PERFIL -> ConfiguracionApoderadoScreen()

                    TabApoderado.AJUSTES -> AjustesScreen(onCerrarSesion = onCerrarSesion)
                }
            }
        }
    }
}
