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
├─ data/                # Capa de datos
│  ├─ models/           # DTOs / modelos de datos (API y tablas locales)
│  ├─ repositories/     # Acceso a datos (hoy con datos de ejemplo)
│  ├─ local/            # Persistencia local: Room / DataStore
│  └─ remote/           # API REST y Firebase
├─ domain/              # Capa de dominio
│  ├─ entities/         # Objetos de negocio (Alumno, Conductor, Hijo…)
│  └─ usecases/         # Casos de uso
├─ ui/                  # Capa de presentación
│  ├─ screens/
│  │  ├─ auth/          # Selección de perfil / login
│  │  ├─ dashboard/     # Pantallas principales + navegación por rol
│  │  ├─ profile/       # Perfiles y configuración
│  │  └─ secure/        # Escaneo QR / cámara
│  ├─ components/       # Widgets reutilizables (avatar, mapa, chips…)
│  └─ theme/            # Paleta de marca y tema Material 3
├─ core/                # Utilidades transversales
│  ├─ utils/            # Formateo, validaciones
│  ├─ permissions/      # Permisos (cámara, ubicación, almacenamiento)
│  └─ security/         # Cifrado, biometría, sesión
└─ services/            # Servicios de plataforma
   ├─ api/              # Cliente HTTP (Retrofit/OkHttp)
   ├─ storage/          # Archivos locales / nube
   └─ camera/           # Cámara y galería
```

> Arquitectura **MVVM + Clean Architecture**. Las carpetas `data/models`,
> `data/local`, `data/remote`, `domain/usecases`, `core/*` y `services/*`
> contienen un archivo *placeholder* que documenta su propósito; se
> implementarán en las siguientes fases.

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
