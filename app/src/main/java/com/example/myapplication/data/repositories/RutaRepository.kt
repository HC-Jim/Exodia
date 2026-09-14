package com.example.myapplication.data.repositories

import com.example.myapplication.BuildConfig
import com.example.myapplication.services.api.DirectionsCliente
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio que calcula la ruta por CALLES entre dos puntos usando la
 * Directions API de Google.
 *
 * Si algo falla (sin internet, o la API no está habilitada), devuelve una
 * línea recta entre los dos puntos, para que la app siga funcionando.
 */
class RutaRepository {

    suspend fun obtenerRuta(origen: LatLng, destino: LatLng): List<LatLng> =
        withContext(Dispatchers.IO) {
            try {
                val respuesta = DirectionsCliente.api.obtenerRuta(
                    origen = "${origen.latitude},${origen.longitude}",
                    destino = "${destino.latitude},${destino.longitude}",
                    key = BuildConfig.MAPS_API_KEY
                )

                if (respuesta.routes.isNotEmpty()) {
                    // Google devuelve la ruta comprimida; PolyUtil la convierte en puntos.
                    PolyUtil.decode(respuesta.routes[0].overviewPolyline.points)
                } else {
                    listOf(origen, destino)   // sin ruta: línea recta
                }
            } catch (e: Exception) {
                listOf(origen, destino)       // error/sin internet: línea recta
            }
        }
}
