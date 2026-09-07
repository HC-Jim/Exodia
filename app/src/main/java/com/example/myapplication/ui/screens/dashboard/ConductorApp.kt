package com.example.myapplication.ui.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.dashboard.NavegadorConductor
import com.example.myapplication.ui.screens.dashboard.RutaConductor
import com.example.myapplication.ui.screens.dashboard.TabConductor
import com.example.myapplication.ui.screens.dashboard.rememberNavegadorConductor
import com.example.myapplication.ui.components.BarraInferiorConductor
import com.example.myapplication.ui.screens.profile.ConfiguracionConductorScreen
import com.example.myapplication.ui.screens.secure.EscanearQRScreen

/**
 * Contenedor del rol Conductor: gestiona la barra inferior, el intercambio de
 * pestañas y la superposición del escáner QR. Es un prototipo de navegación;
 * la lógica de negocio vivirá en ViewModel cuando se conecte la capa de datos.
 */
@Composable
fun ConductorApp(modifier: Modifier = Modifier) {
    val navegador = rememberNavegadorConductor()
    val enEscaner = navegador.overlay == RutaConductor.ESCANEAR_QR

    BackHandler(enabled = enEscaner) { navegador.retroceder() }

    if (enEscaner) {
        // Pantalla completa, sin barra inferior (uso mínimo en campo).
        EscanearQRScreen(
            onRetroceder = { navegador.retroceder() },
            modifier = modifier.fillMaxSize()
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BarraInferiorConductor(navegador) }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (navegador.tab) {
                TabConductor.INICIO -> InicioConductorScreen(
                    onIniciarRuta = { navegador.seleccionarTab(TabConductor.SEGUIMIENTO) },
                    onFinalizarRuta = { navegador.seleccionarTab(TabConductor.COLEGIO) }
                )

                TabConductor.SEGUIMIENTO -> RutaActivaScreen(
                    onVerLista = { navegador.seleccionarTab(TabConductor.COLEGIO) },
                    onEscanearQR = { navegador.abrir(RutaConductor.ESCANEAR_QR) }
                )

                TabConductor.COLEGIO -> AlumnosEntregadosScreen()

                TabConductor.PERFIL -> ConfiguracionConductorScreen(
                    onLlamar = {},
                    onCerrarSesion = {}
                )
            }
        }
    }
}
