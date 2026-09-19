package com.example.myapplication.services.api

// OkHttp = la librería que hace las peticiones HTTP por debajo de Retrofit.
import okhttp3.OkHttpClient
// Interceptor que "escucha" cada petición/respuesta para imprimirla en el Logcat.
import okhttp3.logging.HttpLoggingInterceptor
// Retrofit = la librería que convierte tus interfaces (ApiService) en llamadas HTTP reales.
import retrofit2.Retrofit
// Convierte el JSON de la API en objetos Kotlin (y viceversa) usando Gson.
import retrofit2.converter.gson.GsonConverterFactory
// Nos permite expresar los tiempos de espera en segundos de forma legible.
import java.util.concurrent.TimeUnit

/**
 * Crea UNA sola instancia de Retrofit para toda la app (patrón singleton simple).
 * Desde aquí obtienes `RetrofitCliente.api` para llamar a los endpoints.
 *
 * Al ser `object`, Kotlin lo crea una única vez y lo reutiliza en todas partes:
 * así no abres una conexión nueva cada vez que llamas a la API (más eficiente).
 */
object RetrofitCliente {

    // Dirección base del backend. Todos los endpoints de ApiService se cuelgan de aquí.
    // Debe terminar en "/" para que Retrofit concatene bien las rutas.
    private const val BASE_URL = "https://backend-appescolar.onrender.com/"

    // Interceptor de logs: muestra en el Logcat las peticiones/respuestas (útil para depurar).
    private val logger = crearLogger()

    // Configura el interceptor de logs.
    private fun crearLogger(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        // Level.BODY = registra TODO: URL, cabeceras y el cuerpo (JSON) de la petición y la respuesta.
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }


    // Cliente HTTP configurado. Es el "motor" que Retrofit usará para conectarse.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(logger)                 // Engancha el logger para ver las peticiones en el Logcat.
        .connectTimeout(60, TimeUnit.SECONDS)   // Máx. 60s para ESTABLECER la conexión con el servidor.
        .readTimeout(60, TimeUnit.SECONDS)      // Máx. 60s esperando a que el servidor RESPONDA.
        .writeTimeout(60, TimeUnit.SECONDS)     // Máx. 60s para ENVIAR datos (ej. subir un formulario).
        .build()                                // Construye el OkHttpClient final.

    // La instancia lista para usar: `RetrofitCliente.api.loginUsuario(...)`, etc.
    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)                           // Le dice a Retrofit contra qué servidor apuntar.
        .client(cliente)                             // Usa el OkHttpClient de arriba (con logs y timeouts).
        .addConverterFactory(GsonConverterFactory.create()) // JSON <-> objetos Kotlin automáticamente.
        .build()                                     // Arma el objeto Retrofit.
        .create(ApiService::class.java)              // Genera la implementación real de tu interfaz ApiService.
}
