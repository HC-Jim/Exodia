package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.SanAgustinGold
import com.example.myapplication.ui.theme.SanAgustinRed
import com.example.myapplication.ui.theme.SanAgustinRedDark

/**
 * Escudo del Colegio San Agustín (marcador de posición con las iniciales "SA").
 * Se reemplazará por el logotipo oficial cuando esté disponible como recurso.
 */
@Composable
fun EscudoColegio(
    modifier: Modifier = Modifier,
    tamano: Dp = 72.dp
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(listOf(SanAgustinRed, SanAgustinRedDark))
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "SA",
            color = SanAgustinGold,
            fontWeight = FontWeight.Black,
            fontSize = (tamano.value * 0.4f).sp
        )
    }
}
