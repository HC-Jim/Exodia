package com.example.myapplication.ui.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.components.BarraInferiorConductor
import com.example.myapplication.ui.screens.profile.AjustesScreen
import com.example.myapplication.ui.screens.profile.PerfilConductorScreen
import com.example.myapplication.ui.screens.secure.EscanearQRScreen

/**
 * Contenedor del rol Conductor: barra inferior, pestañas y superposiciones
 * (escáner QR, listado de entregados y contacto con estudiante).
 */
@Composable
fun ConductorApp(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {
    val nav = rememberNavegadorConductor()
    val overlay = nav.overlay

    BackHandler(enabled = overlay != null) { nav.retroceder() }

    if (overlay != null) {
        // Superposiciones a pantalla completa, con su propio botón de retroceso.
        Box(modifier.fillMaxSize()) {
            when (overlay) {
                RutaConductor.ESCANEAR_QR -> EscanearQRScreen(onRetroceder = { nav.retroceder() })
                RutaConductor.ALUMNOS_ENTREGADOS -> AlumnosEntregadosScreen(onRetroceder = { nav.retroceder() })
                RutaConductor.CONTACTO_ESTUDIANTE -> ContactoEstudianteScreen(onRetroceder = { nav.retroceder() })
            }
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BarraInferiorConductor(nav) }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (nav.tab) {
                TabConductor.INICIO -> InicioConductorScreen(
                    onIniciarRuta = { nav.seleccionarTab(TabConductor.SEGUIMIENTO) },
                    onFinalizarRuta = { nav.seleccionarTab(TabConductor.PERFIL) }
                )

                TabConductor.SEGUIMIENTO -> RutaActivaScreen(
                    onVerLista = { nav.abrir(RutaConductor.ALUMNOS_ENTREGADOS) },
                    onEscanearQR = { nav.abrir(RutaConductor.ESCANEAR_QR) },
                    onContactar = { nav.abrir(RutaConductor.CONTACTO_ESTUDIANTE) }
                )

                TabConductor.PERFIL -> PerfilConductorScreen(onCerrarSesion = onCerrarSesion)

                TabConductor.AJUSTES -> AjustesScreen(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}
