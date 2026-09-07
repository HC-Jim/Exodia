package com.example.myapplication.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.EscudoColegio
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onListo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progreso by remember { mutableStateOf(0f) }
    val ancho by animateFloatAsState(targetValue = progreso, animationSpec = tween(1400), label = "carga")

    LaunchedEffect(Unit) {
        progreso = 1f
        delay(1700)
        onListo()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EscudoColegio(tamano = 96.dp)
        Spacer(Modifier.height(20.dp))
        Text("Colegio San Agustín", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(40.dp))
        // Barra de carga
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFF0D9DC))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ancho)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SanAgustinRed)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text("cargando…", color = TextSecondary, fontSize = 13.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashPreview() {
    MyApplicationTheme {
        SplashScreen(onListo = {})
    }
}
