package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

/**
 * Escudo del Colegio San Agustín. Usa el logotipo oficial desde los recursos
 * (res/drawable/logo_colegio.webp).
 */
@Composable
fun EscudoColegio(
    modifier: Modifier = Modifier,
    tamano: Dp = 72.dp
) {
    Image(
        painter = painterResource(id = R.drawable.logo_colegio),
        contentDescription = "Escudo del Colegio San Agustín",
        modifier = modifier.size(tamano),
        contentScale = ContentScale.Fit
    )
}
