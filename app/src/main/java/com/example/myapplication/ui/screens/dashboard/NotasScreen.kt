package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.entities.Nota
import com.example.myapplication.ui.components.EncabezadoConductor
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary

@Composable
fun NotasScreen(
    modifier: Modifier = Modifier,
    onRetroceder: (() -> Unit)? = null,
    // El ViewModel trae las notas desde la API.
    viewModel: NotasViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        EncabezadoConductor(
            titulo = "Notas",
            accionIcono = if (onRetroceder != null) Icons.AutoMirrored.Filled.ArrowBack else null,
            accionDescripcion = "Volver",
            onAccion = onRetroceder
        )

        Text(
            "Segundo bimestre",
            modifier = Modifier.padding(start = 20.dp, top = 4.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Spacer(Modifier.size(12.dp))

        when {
            viewModel.cargando -> Text(
                "Cargando notas…",
                modifier = Modifier.padding(20.dp),
                color = TextSecondary
            )
            viewModel.error != null -> Text(
                viewModel.error!!,
                modifier = Modifier.padding(20.dp),
                color = Color(0xFFEF4444)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(viewModel.notas, key = { it.id }) { nota ->
                    TarjetaNota(nota)
                }
            }
        }
    }
}

@Composable
private fun TarjetaNota(nota: Nota) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(nota.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = nota.color)
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(nota.curso, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
            Text(nota.detalle, color = TextSecondary, fontSize = 13.sp)
        }
        // Nota en círculo
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(nota.color),
            contentAlignment = Alignment.Center
        ) {
            Text(nota.valor, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotasPreview() {
    MyApplicationTheme {
        NotasScreen()
    }
}
