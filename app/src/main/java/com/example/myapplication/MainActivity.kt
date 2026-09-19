package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myapplication.core.utils.AppSettings
import com.example.myapplication.core.utils.Sesion
import com.example.myapplication.data.local.PreferenciasRepository
import com.example.myapplication.ui.screens.auth.LoginScreen
import com.example.myapplication.ui.screens.auth.RecuperarScreen
import com.example.myapplication.ui.screens.auth.RegistroScreen
import com.example.myapplication.ui.screens.auth.SplashScreen
import com.example.myapplication.ui.screens.auth.TwoFactorScreen
import com.example.myapplication.ui.screens.dashboard._navegacion.ApoderadoApp
import com.example.myapplication.ui.screens.dashboard._navegacion.ConductorApp
import com.example.myapplication.ui.theme.MyApplicationTheme

// Pantallas por las que pasa la app. La navegación se controla cambiando de fase.
private enum class FaseApp { SPLASH, LOGIN, REGISTRO, RECUPERAR, DOS_PASOS, CONDUCTOR, ESTUDIANTE }

// Única Activity de la app; todo lo demás son composables de Jetpack Compose.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()   // permite dibujar detrás de las barras del sistema

        // Restaura preferencias guardadas antes de dibujar, para que el tema arranque correcto.
        val prefs = PreferenciasRepository(this)
        val ajustes = kotlinx.coroutines.runBlocking { prefs.leerAjustes() }
        AppSettings.modoOscuro = ajustes.modoOscuro
        AppSettings.escalaTexto = ajustes.escalaTexto
        AppSettings.silenciarNotificaciones = ajustes.silenciarNotificaciones

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRaiz()
                }
            }
        }
    }
}

// Raíz de la navegación: según la fase actual, muestra la pantalla correspondiente.
// Cada pantalla avisa con callbacks (onX) hacia dónde ir, cambiando 'fase'.
@Composable
private fun AppRaiz() {
    var fase by remember { mutableStateOf(FaseApp.SPLASH) }

    when (fase) {
        FaseApp.SPLASH -> SplashScreen(onListo = { fase = FaseApp.LOGIN })
        FaseApp.LOGIN -> LoginScreen(
            onIniciar = { fase = FaseApp.DOS_PASOS },
            onRegistrar = { fase = FaseApp.REGISTRO },
            onOlvide = { fase = FaseApp.RECUPERAR }
        )
        FaseApp.REGISTRO -> RegistroScreen(
            onRegistrado = { fase = FaseApp.LOGIN },
            onVolver = { fase = FaseApp.LOGIN }
        )
        FaseApp.RECUPERAR -> RecuperarScreen(
            onListo = { fase = FaseApp.LOGIN },
            onVolver = { fase = FaseApp.LOGIN }
        )
        FaseApp.DOS_PASOS -> TwoFactorScreen(
            // Tras validar, entra directo al rol de la cuenta (Estudiante o Conductor).
            onValidar = {
                fase = if (Sesion.usuario?.rol == "CONDUCTOR") FaseApp.CONDUCTOR else FaseApp.ESTUDIANTE
            },
            onCancelar = { fase = FaseApp.LOGIN }
        )
        FaseApp.CONDUCTOR -> ConductorApp(onCerrarSesion = { fase = FaseApp.LOGIN })
        FaseApp.ESTUDIANTE -> ApoderadoApp(onCerrarSesion = { fase = FaseApp.LOGIN })
    }
}
