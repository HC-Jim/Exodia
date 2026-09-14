package com.example.myapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.AccentBlue
import com.example.myapplication.ui.theme.DangerRed
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.SuccessGreen
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

/**
 * Recuperación de contraseña en 2 pasos:
 *  1) Ingresar el correo -> se muestra la pregunta de seguridad.
 *  2) Responder la pregunta y escribir la nueva contraseña -> se restablece.
 */
@Composable
fun RecuperarScreen(
    onListo: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var respuesta by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }

    val hayPregunta = viewModel.pregunta != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text("Recuperar contraseña", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SanAgustinRed)
        Spacer(Modifier.height(16.dp))

        // Paso 1: correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !hayPregunta,
            shape = RoundedCornerShape(12.dp),
            label = { Text("Correo de tu cuenta") }
        )
        Spacer(Modifier.height(10.dp))

        if (!hayPregunta) {
            Button(
                onClick = { viewModel.cargarPregunta(correo) },
                enabled = !viewModel.cargando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(if (viewModel.cargando) "Buscando…" else "Continuar", fontWeight = FontWeight.SemiBold)
            }
        } else {
            // Paso 2: pregunta + respuesta + nueva contraseña
            Text("Pregunta de seguridad:", color = TextSecondary, fontSize = 13.sp)
            Text(viewModel.pregunta!!, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = respuesta,
                onValueChange = { respuesta = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Tu respuesta") }
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = nueva,
                onValueChange = { nueva = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Nueva contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.restablecer(correo, respuesta, nueva) { onListo() } },
                enabled = !viewModel.cargando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text(if (viewModel.cargando) "Guardando…" else "Restablecer contraseña", fontWeight = FontWeight.SemiBold)
            }
        }

        if (viewModel.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(viewModel.error!!, color = DangerRed, fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onVolver) { Text("Volver a iniciar sesión", color = TextSecondary) }
    }
}
