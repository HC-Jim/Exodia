package com.example.myapplication.ui.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.repositories.MockApoderado
import com.example.myapplication.ui.screens.dashboard.RutaApoderado
import com.example.myapplication.ui.screens.dashboard.TabApoderado
import com.example.myapplication.ui.screens.dashboard.rememberNavegadorApoderado
import com.example.myapplication.ui.screens.profile.ConfiguracionApoderadoScreen
import com.example.myapplication.ui.screens.profile.PerfilAlumnoScreen
import com.example.myapplication.ui.theme.IndigoPrimary
import com.example.myapplication.ui.theme.SurfaceMuted
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun ApoderadoApp(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit = {}
) {
    val nav = rememberNavegadorApoderado()
    val overlay = nav.overlay

    BackHandler(enabled = overlay != null) { nav.retroceder() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (overlay == null) BarraInferior(
                seleccionado = nav.tab,
                onSelect = { nav.seleccionarTab(it) }
            )
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (overlay) {
                RutaApoderado.PERFIL_HIJO -> PerfilAlumnoScreen(
                    hijo = nav.hijoActivo,
                    onRetroceder = { nav.retroceder() },
                    onHijoClick = { nav.abrir(RutaApoderado.PERFIL_HIJO, it) },
                    onNotas = { nav.abrir(RutaApoderado.NOTAS) },
                    onAsistencias = { nav.abrir(RutaApoderado.ASISTENCIAS) },
                    onCerrarSesion = onCerrarSesion
                )

                RutaApoderado.NOTAS -> NotasScreen(onRetroceder = { nav.retroceder() })

                RutaApoderado.ASISTENCIAS -> AsistenciasScreen(onRetroceder = { nav.retroceder() })

                RutaApoderado.EVENTO -> EventoDetalleScreen(onRetroceder = { nav.retroceder() })

                null -> when (nav.tab) {
                    TabApoderado.INICIO -> InicioApoderadoScreen(
                        onMovilidad = { nav.seleccionarTab(TabApoderado.SEGUIMIENTO) },
                        onColegio = { nav.seleccionarTab(TabApoderado.COLEGIO) },
                        onCalendario = { nav.seleccionarTab(TabApoderado.COLEGIO) },
                        onAsistencias = { nav.abrir(RutaApoderado.ASISTENCIAS) },
                        onHijoClick = { nav.abrir(RutaApoderado.PERFIL_HIJO, it) }
                    )

                    TabApoderado.SEGUIMIENTO -> SeguimientoApoderadoScreen()

                    TabApoderado.COLEGIO -> ComunicadosScreen(
                        onComunicadoClick = { nav.abrir(RutaApoderado.EVENTO) }
                    )

                    TabApoderado.PERFIL -> ConfiguracionApoderadoScreen()
                }
            }
        }
    }
}

@Composable
private fun BarraInferior(seleccionado: TabApoderado, onSelect: (TabApoderado) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        TabApoderado.entries.forEach { destino ->
            NavigationBarItem(
                selected = destino == seleccionado,
                onClick = { onSelect(destino) },
                icon = {
                    Icon(destino.icono, contentDescription = destino.etiqueta, modifier = Modifier.size(24.dp))
                },
                label = { Text(destino.etiqueta, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IndigoPrimary,
                    selectedTextColor = IndigoPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = SurfaceMuted
                )
            )
        }
    }
}
