package com.example.myapplication.services.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Cliente de la Directions API de Google (servicio web).
 * Devuelve la ruta por CALLES entre dos puntos (origen -> destino).
 */

// --- Modelos que mapean la respuesta JSON de Google ---
data class DirectionsRespuesta(
    val routes: List<RutaDirections>
)

data class RutaDirections(
    @SerializedName("overview_polyline") val overviewPolyline: PuntosPolyline
)

data class PuntosPolyline(
    val points: String   // ruta comprimida (se decodifica con PolyUtil)
)

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun obtenerRuta(
        @Query("origin") origen: String,        // "lat,lng"
        @Query("destination") destino: String,  // "lat,lng"
        @Query("key") key: String,
        @Query("mode") modo: String = "driving"
    ): DirectionsRespuesta
}

/** Retrofit apuntando al servidor de Google Maps. */
object DirectionsCliente {
    val api: DirectionsApiService = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DirectionsApiService::class.java)
}
