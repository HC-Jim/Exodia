# App de Transporte y Gestión Escolar — Grupo Exodia

Aplicación Android para el transporte escolar de una institución educativa.
Tiene dos roles:

- **Estudiante:** ve comunicados, notas y asistencias, sigue el bus en el mapa,
  y guarda contactos de emergencia y recordatorios personales.
- **Conductor:** gestiona la ruta del día, marca a cada estudiante como entregado
  o cancelado y comparte su ubicación para el seguimiento.

Está desarrollada con Kotlin y Jetpack Compose, siguiendo una arquitectura MVVM.

## Estructura del proyecto

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt        # punto de entrada y navegación entre pantallas
├── core/                  # utilidades, permisos, sesión y ajustes en memoria
├── data/                  # capa de datos
│   ├── local/             # SQLite y DataStore (persistencia local)
│   ├── models/            # DTOs que representan el JSON de la API
│   ├── remote/            # conversión de DTO a entidades del dominio
│   └── repositories/      # repositorios (única puerta a los datos)
├── domain/
│   └── entities/          # entidades de negocio (Alumno, Usuario, ...)
├── services/
│   └── api/               # cliente Retrofit
└── ui/
    ├── components/         # componentes reutilizables
    ├── screens/            # pantallas (auth, dashboard, profile, secure)
    └── theme/              # colores y tipografía
```

## Arquitectura

El flujo de datos es siempre el mismo:

```
Pantalla (Compose) → ViewModel → Repository → fuente de datos (API o local)
```

La pantalla no accede directamente a la base de datos ni a la red: siempre pasa
por su ViewModel y por un Repository.

## Persistencia local

- **DataStore:** ajustes de apariencia (modo oscuro, tamaño de letra).
- **SQLite:** contactos de emergencia, recordatorios personales, caché de la
  información de la API, cola de cambios sin conexión e historial de entregas.

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- Retrofit + Gson (consumo de la API REST)
- DataStore y SQLite (persistencia local)
- Google Maps (seguimiento del bus)
- SDK mínimo 24 / objetivo 36

## Configuración

1. Clonar el repositorio y abrirlo en Android Studio.

2. En `local.properties`, agregar la clave de Google Maps:

   ```
   MAPS_API_KEY=tu_clave
   ```

3. En `services/api/RetrofitCliente.kt`, poner la URL del backend
   (la de Render, o `http://10.0.2.2:3000/` para el backend local en el emulador).

4. Sincronizar Gradle y ejecutar en un emulador o dispositivo con API 24 o superior.

## Backend

La API que consume la app está en un repositorio aparte, hecha con
Node.js + Express + Supabase.

## Autores — Grupo Exodia

- Jose Hugo Adrian Caycho Salazar
- Alex Roberto Suma Condori
- Jaime Javier Espinoza Quispe
- Guido Alberto Arellano Cerna
- Jim Jose Andres Huarcaya Chino
- Jean Carlos Alberto Sudario Manrique
