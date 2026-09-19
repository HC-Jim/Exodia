package com.example.myapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.EscudoColegio
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextSecondary
import kotlinx.coroutines.delay

// Pantalla de bienvenida: muestra el escudo unos segundos y pasa al login.
@Composable
fun SplashScreen(
    onListo: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Espera un momento (1.7 s) y luego avisa que ya terminó de "cargar".
    LaunchedEffect(Unit) {
        delay(2000)
        onListo()
    }

    Column(
        modifier = modifier
            .fillMaxSize() // ocupa toda la pantalla
            .background(MaterialTheme.colorScheme.background) // color de fondo del tema
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,   // centra en vertical 
        horizontalAlignment = Alignment.CenterHorizontally  // centra en horizontarl
    ) {
        EscudoColegio(tamano = 96.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            "Colegio San Agustín",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        Text("Cargando…", color = TextSecondary, fontSize = 14.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashPreview() {
    MyApplicationTheme {
        SplashScreen(onListo = {})
    }
}
