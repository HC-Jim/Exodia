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
import com.example.myapplication.ui.screens.auth.Rol
import com.example.myapplication.ui.screens.auth.SelectorRolScreen
import com.example.myapplication.ui.screens.dashboard.ApoderadoApp
import com.example.myapplication.ui.screens.dashboard.ConductorApp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRaiz()
                }
            }
        }
    }
}

@Composable
private fun AppRaiz() {
    var rol by remember { mutableStateOf(Rol.NINGUNO) }
    when (rol) {
        Rol.NINGUNO -> SelectorRolScreen(onRol = { rol = it })
        Rol.CONDUCTOR -> ConductorApp()
        Rol.APODERADO -> ApoderadoApp()
    }
}
