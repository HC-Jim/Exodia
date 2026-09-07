# App de Transporte y Asistencia Escolar — Grupo Exodia

Aplicación móvil nativa Android para la gestión de asistencia y transporte escolar
en una institución educativa de nivel básico. Este repositorio contiene, por ahora,
las **interfaces del rol Conductor** construidas con Jetpack Compose.

> Trabajo de Campo N°2 — Desarrollo de Aplicaciones Móviles.
> El proyecto atiende a tres perfiles de usuario: **Docente**, **Apoderado** y **Conductor**.

---

## Estado actual

- ✅ Interfaces del rol **Conductor** (5 pantallas) con datos de ejemplo.
- ⏳ Pendiente: lógica de negocio (ViewModels), capa de datos y roles Docente / Apoderado.

> Las pantallas usan datos simulados (`MockConductor`). El mapa y el escáner QR son
> marcadores visuales; se reemplazarán por **Google Maps SDK** y **CameraX / ML Kit**
> al conectar la capa de datos.

---

## Pantallas del Conductor

| Pantalla | Descripción |
|---|---|
| **Inicio** | Saludo, vista previa del mapa y acciones *Iniciar ruta* / *Finaliza Ruta*. |
| **Configuración / Perfil** | Datos del conductor, movilidad, paradero, contacto y cerrar sesión. |
| **Escanear QR** | Visor de cámara para registrar el abordaje del alumno. |
| **Ruta Activa** | Mapa con el bus y los alumnos, próxima entrega y acceso al escáner. |
| **Alumnos Entregados** | Lista de alumnos entregados con su estado. |

Navegación inferior con cuatro destinos: **Inicio · Seguimiento · Colegio · Perfil**.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.x |
| Interfaz | Jetpack Compose + Material Design 3 |
| Arquitectura (objetivo) | MVVM + Clean Architecture |
| SDK mínimo / objetivo | API 24 / API 36 |

Tecnologías previstas para las siguientes fases: Coroutines + Flow, Hilt, Room,
DataStore, Retrofit/OkHttp/Moshi, Firebase (Auth, Firestore, Storage, Cloud Messaging),
Google Maps SDK y WorkManager.

---

## Estructura del proyecto

```
app/src/main/java/com/example/myapplication/
├─ MainActivity.kt
├─ conductor/
│  ├─ model/            # Modelos de presentación y datos de ejemplo
│  ├─ navigation/       # Navegador por pestañas (sin dependencias externas)
│  └─ ui/
│     ├─ InicioConductorScreen.kt
│     ├─ ConfiguracionConductorScreen.kt
│     ├─ EscanearQRScreen.kt
│     ├─ RutaActivaScreen.kt
│     ├─ AlumnosEntregadosScreen.kt
│     ├─ ConductorApp.kt      # Scaffold + barra inferior
│     └─ componentes/         # Avatar, mapa simulado, encabezados, chips
└─ ui/theme/            # Paleta de marca y tema Material 3
```

---

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en **Android Studio** (última versión estable).
2. Esperar la sincronización de Gradle (**Sync Project with Gradle Files**).
3. Ejecutar en un emulador o dispositivo con **API 24 o superior**.

Cada pantalla incluye una función `@Preview`, por lo que puede visualizarse
directamente en el panel *Design* del IDE sin ejecutar la app.

---

## Consideraciones de diseño

- Tema claro de alto contraste, pensado para uso en exteriores.
- Estados indicados por **ícono + texto**, no solo por color (accesibilidad).
- Áreas táctiles de al menos **48 dp**.
- Listas con `LazyColumn` y claves estables para un desplazamiento fluido.

---

## Autores — Grupo Exodia

- Jose Hugo Adrian Caycho Salazar
- Alex Roberto Suma Condori
- Jaime Javier Espinoza Quispe
- Guido Alberto Arellano Cerna
- Jim Jose Andres Huarcaya Chino
- Jean Carlos Alberto Sudario Manrique
