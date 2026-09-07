package com.example.myapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.EscudoColegio
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.Divider
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun TwoFactorScreen(
    onValidar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dígitos de ejemplo pre-cargados para el prototipo.
    var llenos by remember { mutableIntStateOf(4) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EscudoColegio(tamano = 76.dp)
        Spacer(Modifier.height(18.dp))
        Text(
            "Validación de dos pasos",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = SanAgustinRed,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Ingresa el código PIN de 6 dígitos que enviamos a tu correo registrado (usu***@colegio.edu.pe).",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))

        // Casillas del PIN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(6) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.5.dp, if (i < llenos) AccentBlue else Divider, RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { llenos = i + 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (i < llenos) "•" else "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Reenviar código (disponible en 55s)", color = TextSecondary, fontSize = 12.sp)
        Text("¿No recibiste el código? Contáctanos", color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onValidar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Validar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
        TextButton(onClick = onCancelar) { Text("Cancelar", color = AccentBlue) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TwoFactorPreview() {
    MyApplicationTheme {
        TwoFactorScreen(onValidar = {}, onCancelar = {})
    }
}
