package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.core.utils.AppSettings
import com.example.myapplication.core.utils.Sesion
import com.example.myapplication.data.local.PreferenciasRepository
import com.example.myapplication.ui.screens.auth.LoginScreen
import com.example.myapplication.ui.screens.auth.RecuperarScreen
import com.example.myapplication.ui.screens.auth.RegistroScreen
import com.example.myapplication.ui.screens.auth.Rol
import com.example.myapplication.ui.screens.auth.SelectorRolScreen
import com.example.myapplication.ui.screens.auth.SplashScreen
import com.example.myapplication.ui.screens.auth.TwoFactorScreen
import com.example.myapplication.ui.screens.dashboard.ApoderadoApp
import com.example.myapplication.ui.screens.dashboard.ConductorApp
import com.example.myapplication.ui.theme.MyApplicationTheme

private enum class FaseApp { SPLASH, LOGIN, REGISTRO, RECUPERAR, DOS_PASOS, SELECTOR, CONDUCTOR, ESTUDIANTE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Restaura preferencias guardadas
        val prefs = PreferenciasRepository(this)
        val ajustes = kotlinx.coroutines.runBlocking { prefs.leerAjustes() }
        AppSettings.modoOscuro = ajustes.modoOscuro
        AppSettings.escalaTexto = ajustes.escalaTexto
        AppSettings.mantenerPantalla = ajustes.mantenerPantalla
        AppSettings.silenciarNotificaciones = ajustes.silenciarNotificaciones

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MantenerPantallaEncendida()
                    AppRaiz()
                }
            }
        }
    }
}

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
        FaseApp.SELECTOR -> SelectorRolScreen(onRol = { rol ->
            if (rol == Rol.CONDUCTOR) {
                fase = FaseApp.CONDUCTOR
            } else {
                fase = FaseApp.ESTUDIANTE
            }
        })
        FaseApp.CONDUCTOR -> ConductorApp(onCerrarSesion = { fase = FaseApp.LOGIN })
        FaseApp.ESTUDIANTE -> ApoderadoApp(onCerrarSesion = { fase = FaseApp.LOGIN })
    }
}

/** Aplica el flag de "mantener pantalla encendida" según los ajustes. */
@Composable
private fun MantenerPantallaEncendida() {
    val context = LocalContext.current
    val activo = AppSettings.mantenerPantalla
    DisposableEffect(activo) {
        val window = (context as? Activity)?.window
        if (activo) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
}
