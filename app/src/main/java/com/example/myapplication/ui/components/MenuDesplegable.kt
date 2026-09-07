package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.IndigoPrimary

/** Una opción del menú desplegable de navegación. */
data class OpcionMenu(
    val etiqueta: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

/**
 * Botón de menú (hamburguesa) que despliega la navegación principal.
 * Corresponde a la "Vista Desplegable" del prototipo.
 */
@Composable
fun MenuDesplegable(
    opciones: List<OpcionMenu>,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }

    Box(modifier) {
        IconButton(onClick = { abierto = true }) {
            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú", tint = IndigoPrimary)
        }
        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.etiqueta) },
                    onClick = {
                        abierto = false
                        opcion.onClick()
                    },
                    leadingIcon = {
                        Icon(opcion.icono, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                    }
                )
            }
        }
    }
}
