package com.example.myapplication.services.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Crea UNA sola instancia de Retrofit para toda la app (patrón singleton simple).
 * Desde aquí obtienes `RetrofitCliente.api` para llamar a los endpoints.
 */
object RetrofitCliente {

    // ⬇️ CAMBIA ESTA URL por la de tu servicio en Render (termina con "/").
    //    - En el emulador de Android, para tu PC local usa: "http://10.0.2.2:3000/"
    //    - En producción (Render):                          "https://TU-APP.onrender.com/"
    private const val BASE_URL = "https://backend-appescolar.onrender.com/"

    // Muestra en el Logcat las peticiones/respuestas (útil para depurar).
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
