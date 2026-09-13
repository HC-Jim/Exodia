-- =====================================================================
--  Colegio San Agustín — App de Transporte y Asistencia Escolar
--  Modelo de datos relacional — versión PostgreSQL (Supabase compatible)
--  Grupo Exodia — Trabajo de Campo 2
--
--  Ejecuta este script en tu base ya existente (esquema public).
--  Los ENUM se implementan como VARCHAR + CHECK para máxima portabilidad.
--  La posición del vehículo en tiempo real vive en Firestore, no aquí.
-- =====================================================================

-- Limpieza idempotente (respeta dependencias con CASCADE)
DROP TABLE IF EXISTS notificacion, indicador_recurso, abordaje, ruta_paradero, ruta,
    evento_participacion, evento, comunicado, nota, justificacion, asistencia,
    apoderado_estudiante, estudiante, paradero, vehiculo, conductor, apoderado,
    curso, aula, codigo_verificacion, usuario CASCADE;

-- =====================================================================
--  1. SEGURIDAD / USUARIOS
-- =====================================================================

CREATE TABLE usuario (
    id             SERIAL PRIMARY KEY,
    correo         VARCHAR(120) NOT NULL UNIQUE,
    -- La app NO almacena contraseñas planas (Firebase Auth): solo referencia/hash.
    password_hash  VARCHAR(255),
    rol            VARCHAR(12)  NOT NULL CHECK (rol IN ('APODERADO','CONDUCTOR','ADMIN')),
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- PIN de validación de dos pasos (2FA)
CREATE TABLE codigo_verificacion (
    id          SERIAL PRIMARY KEY,
    usuario_id  INT       NOT NULL REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE,
    codigo      CHAR(6)   NOT NULL,
    expira_en   TIMESTAMP NOT NULL,
    usado       BOOLEAN   NOT NULL DEFAULT FALSE,
    creado_en   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
--  2. ESTRUCTURA ACADÉMICA
-- =====================================================================

CREATE TABLE aula (
    id       SERIAL PRIMARY KEY,
    grado    VARCHAR(20) NOT NULL,
    seccion  VARCHAR(5)  NOT NULL,
    nivel    VARCHAR(12) NOT NULL CHECK (nivel IN ('INICIAL','PRIMARIA','SECUNDARIA')),
    UNIQUE (grado, seccion, nivel)
);

CREATE TABLE curso (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(80) NOT NULL UNIQUE
);

-- =====================================================================
--  3. PERSONAS
-- =====================================================================

CREATE TABLE apoderado (
    id                   SERIAL PRIMARY KEY,
    usuario_id           INT NOT NULL UNIQUE REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE,
    nombre               VARCHAR(120) NOT NULL,
    telefono             VARCHAR(30),
    correo               VARCHAR(120),
    direccion            VARCHAR(160),
    contacto_emergencia  VARCHAR(30)
);

CREATE TABLE conductor (
    id                   SERIAL PRIMARY KEY,
    usuario_id           INT NOT NULL UNIQUE REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE,
    nombre               VARCHAR(120) NOT NULL,
    dni                  VARCHAR(15)  NOT NULL UNIQUE,
    celular              VARCHAR(30),
    correo               VARCHAR(120),
    contacto_emergencia  VARCHAR(30),
    licencia             VARCHAR(30)  NOT NULL,
    zona_asignada        VARCHAR(40)
);

CREATE TABLE vehiculo (
    id            SERIAL PRIMARY KEY,
    numero        VARCHAR(30) NOT NULL,
    placa         VARCHAR(10) NOT NULL UNIQUE,
    capacidad     INT         NOT NULL DEFAULT 15,
    conductor_id  INT REFERENCES conductor(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE paradero (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(80)  NOT NULL,
    direccion  VARCHAR(160),
    latitud    NUMERIC(10,7),
    longitud   NUMERIC(10,7)
);

CREATE TABLE estudiante (
    id           SERIAL PRIMARY KEY,
    nombres      VARCHAR(80)  NOT NULL,
    apellidos    VARCHAR(80)  NOT NULL,
    codigo_qr    VARCHAR(40)  NOT NULL UNIQUE,
    foto_url     VARCHAR(255),
    aula_id      INT NOT NULL REFERENCES aula(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    vehiculo_id  INT REFERENCES vehiculo(id) ON DELETE SET NULL ON UPDATE CASCADE,
    paradero_id  INT REFERENCES paradero(id) ON DELETE SET NULL ON UPDATE CASCADE,
    activo       BOOLEAN NOT NULL DEFAULT TRUE
);

-- Un estudiante puede tener uno o más apoderados (M:N)
CREATE TABLE apoderado_estudiante (
    apoderado_id   INT NOT NULL REFERENCES apoderado(id)  ON DELETE CASCADE ON UPDATE CASCADE,
    estudiante_id  INT NOT NULL REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE,
    parentesco     VARCHAR(6) NOT NULL DEFAULT 'TUTOR' CHECK (parentesco IN ('PADRE','MADRE','TUTOR','OTRO')),
    PRIMARY KEY (apoderado_id, estudiante_id)
);

-- =====================================================================
--  4. ASISTENCIA Y JUSTIFICACIONES
-- =====================================================================

CREATE TABLE asistencia (
    id             SERIAL PRIMARY KEY,
    estudiante_id  INT  NOT NULL REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE,
    fecha          DATE NOT NULL,
    estado         VARCHAR(12) NOT NULL CHECK (estado IN ('PRESENTE','TARDANZA','FALTA','JUSTIFICADO')),
    registrado_por INT REFERENCES usuario(id) ON DELETE SET NULL ON UPDATE CASCADE,
    sincronizado   BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (estudiante_id, fecha)
);

CREATE TABLE justificacion (
    id            SERIAL PRIMARY KEY,
    asistencia_id INT NOT NULL UNIQUE REFERENCES asistencia(id) ON DELETE CASCADE ON UPDATE CASCADE,
    motivo        VARCHAR(255) NOT NULL,
    evidencia_url VARCHAR(255),
    estado        VARCHAR(10) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE','APROBADA','RECHAZADA')),
    creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
--  5. NOTAS
-- =====================================================================

CREATE TABLE nota (
    id             SERIAL PRIMARY KEY,
    estudiante_id  INT NOT NULL REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE,
    curso_id       INT NOT NULL REFERENCES curso(id)      ON DELETE CASCADE ON UPDATE CASCADE,
    periodo        VARCHAR(30)  NOT NULL,
    valor          NUMERIC(4,1) NOT NULL CHECK (valor >= 0 AND valor <= 20),
    detalle        VARCHAR(120),
    UNIQUE (estudiante_id, curso_id, periodo)
);

-- =====================================================================
--  6. COMUNICADOS Y EVENTOS
-- =====================================================================

CREATE TABLE comunicado (
    id         SERIAL PRIMARY KEY,
    titulo     VARCHAR(120) NOT NULL,
    detalle    VARCHAR(255),
    fecha      DATE NOT NULL,
    tipo       VARCHAR(12) NOT NULL DEFAULT 'COMUNICADO' CHECK (tipo IN ('COMUNICADO','CIRCULAR')),
    aula_id    INT REFERENCES aula(id) ON DELETE SET NULL ON UPDATE CASCADE,  -- NULL = toda la institución
    creado_en  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE evento (
    id           SERIAL PRIMARY KEY,
    titulo       VARCHAR(120) NOT NULL,
    descripcion  TEXT,
    fecha        DATE NOT NULL,
    hora_inicio  TIME,
    hora_fin     TIME,
    lugar        VARCHAR(160),
    imagen_url   VARCHAR(255),
    creado_en    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE evento_participacion (
    id            SERIAL PRIMARY KEY,
    evento_id     INT NOT NULL REFERENCES evento(id)    ON DELETE CASCADE ON UPDATE CASCADE,
    apoderado_id  INT NOT NULL REFERENCES apoderado(id) ON DELETE CASCADE ON UPDATE CASCADE,
    estado        VARCHAR(12) NOT NULL DEFAULT 'GUARDADO' CHECK (estado IN ('GUARDADO','ASISTIRA','NO_ASISTIRA')),
    creado_en     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (evento_id, apoderado_id)
);

-- =====================================================================
--  7. TRANSPORTE
-- =====================================================================

CREATE TABLE ruta (
    id                SERIAL PRIMARY KEY,
    conductor_id      INT  NOT NULL REFERENCES conductor(id) ON DELETE CASCADE ON UPDATE CASCADE,
    vehiculo_id       INT  NOT NULL REFERENCES vehiculo(id)  ON DELETE CASCADE ON UPDATE CASCADE,
    fecha             DATE NOT NULL,
    estado            VARCHAR(12) NOT NULL DEFAULT 'INACTIVA' CHECK (estado IN ('INACTIVA','ACTIVA','FINALIZADA')),
    hora_inicio       TIMESTAMP,
    hora_fin          TIMESTAMP,
    ult_latitud       NUMERIC(10,7),
    ult_longitud      NUMERIC(10,7),
    ult_actualizacion TIMESTAMP
);

CREATE TABLE ruta_paradero (
    ruta_id      INT NOT NULL REFERENCES ruta(id)     ON DELETE CASCADE ON UPDATE CASCADE,
    paradero_id  INT NOT NULL REFERENCES paradero(id) ON DELETE CASCADE ON UPDATE CASCADE,
    orden        INT NOT NULL,
    PRIMARY KEY (ruta_id, paradero_id)
);

CREATE TABLE abordaje (
    id             SERIAL PRIMARY KEY,
    ruta_id        INT NOT NULL REFERENCES ruta(id)       ON DELETE CASCADE ON UPDATE CASCADE,
    estudiante_id  INT NOT NULL REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE,
    tipo           VARCHAR(10) NOT NULL CHECK (tipo IN ('RECOGIDA','ENTREGA')),
    estado         VARCHAR(14) NOT NULL DEFAULT 'PENDIENTE'
                   CHECK (estado IN ('PENDIENTE','ABORDO','ENTREGADO','AUSENTE','NO_CONTACTADO')),
    hora           TIMESTAMP,
    incidencia     VARCHAR(200),
    sincronizado   BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (ruta_id, estudiante_id, tipo)
);

-- =====================================================================
--  8. INDICADORES ODS 12 Y NOTIFICACIONES
-- =====================================================================

CREATE TABLE indicador_recurso (
    id                    SERIAL PRIMARY KEY,
    fecha                 DATE NOT NULL UNIQUE,
    hojas_no_impresas     INT          NOT NULL DEFAULT 0,
    emisiones_evitadas_kg NUMERIC(8,3) NOT NULL DEFAULT 0,
    km_ruta               NUMERIC(8,2) NOT NULL DEFAULT 0
);

CREATE TABLE notificacion (
    id          SERIAL PRIMARY KEY,
    usuario_id  INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE,
    titulo      VARCHAR(120) NOT NULL,
    mensaje     VARCHAR(255),
    tipo        VARCHAR(12) NOT NULL DEFAULT 'GENERAL'
                CHECK (tipo IN ('ABORDAJE','INASISTENCIA','COMUNICADO','EVENTO','GENERAL')),
    leida       BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices de apoyo
CREATE INDEX idx_asistencia_fecha    ON asistencia(fecha);
CREATE INDEX idx_abordaje_estado     ON abordaje(estado);
CREATE INDEX idx_estudiante_vehiculo ON estudiante(vehiculo_id);
CREATE INDEX idx_comunicado_fecha    ON comunicado(fecha);


-- =====================================================================
--  DATOS DE PRUEBA (SEED)
-- =====================================================================

INSERT INTO usuario (id, correo, password_hash, rol) VALUES
  (1, 'apoderado@colegio.edu.pe', NULL, 'APODERADO'),
  (2, 'conductor@colegio.edu.pe', NULL, 'CONDUCTOR'),
  (3, 'carlos.garcia@colegio.edu.pe', NULL, 'CONDUCTOR'),
  (4, 'admin@colegio.edu.pe',     NULL, 'ADMIN');

INSERT INTO aula (id, grado, seccion, nivel) VALUES
  (1, '5°', 'A', 'PRIMARIA'),
  (2, '1°', 'A', 'PRIMARIA');

INSERT INTO curso (id, nombre) VALUES
  (1, 'Matemática'),
  (2, 'Comunicación'),
  (3, 'Ciencia y Tecnología'),
  (4, 'Personal Social'),
  (5, 'Arte y Cultura');

INSERT INTO apoderado (id, usuario_id, nombre, telefono, correo, direccion, contacto_emergencia) VALUES
  (1, 1, 'Marco Zuñiga', '(+51) 987 654 121', 'marco.zuniga@email.com', 'Av. Principal 128', '987 111 222');

INSERT INTO conductor (id, usuario_id, nombre, dni, celular, correo, contacto_emergencia, licencia, zona_asignada) VALUES
  (1, 2, 'Rosa Melano',  '78445564', '961541515151', 'rosa.melano@colegio.edu.pe',  '954784548', 'Ad4c548u4d', 'RUTA 2'),
  (2, 3, 'Carlos García','45128796', '999888777',    'carlos.garcia@colegio.edu.pe','954112233', 'C-90887766', 'RUTA 1');

INSERT INTO vehiculo (id, numero, placa, capacidad, conductor_id) VALUES
  (1, 'Movilidad N°04', 'ABC-123', 15, 2),
  (2, 'Movilidad N°02', 'XDS-456', 15, 1);

INSERT INTO paradero (id, nombre, direccion, latitud, longitud) VALUES
  (1, 'Paradero Av. Principal', 'Av. Principal 128',  -12.0463700, -77.0427800),
  (2, 'Paradero Los Álamos',    'Av. Los Álamos 145', -12.0480000, -77.0450000),
  (3, 'Paradero Magnolia',      'Calle Magnolia 128', -12.0500000, -77.0470000);

INSERT INTO estudiante (id, nombres, apellidos, codigo_qr, aula_id, vehiculo_id, paradero_id) VALUES
  (1, 'Julio', 'Zuñiga', 'QR-JULIO-0001', 1, 1, 1),
  (2, 'María', 'Zuñiga', 'QR-MARIA-0002', 2, 1, 1);

INSERT INTO apoderado_estudiante (apoderado_id, estudiante_id, parentesco) VALUES
  (1, 1, 'PADRE'),
  (1, 2, 'PADRE');

INSERT INTO nota (estudiante_id, curso_id, periodo, valor, detalle) VALUES
  (1, 1, 'II Bimestre', 18.0, 'Examen bimestral'),
  (1, 2, 'II Bimestre', 17.0, 'Comprensión lectora'),
  (1, 3, 'II Bimestre', 20.0, 'Proyecto de feria'),
  (1, 4, 'II Bimestre', 16.0, 'Trabajo grupal'),
  (1, 5, 'II Bimestre', 19.0, 'Exposición');

INSERT INTO asistencia (id, estudiante_id, fecha, estado, registrado_por) VALUES
  (1, 1, '2026-09-07', 'PRESENTE',    4),
  (2, 1, '2026-09-08', 'FALTA',       4),
  (3, 1, '2026-09-09', 'TARDANZA',    4),
  (4, 1, '2026-09-10', 'JUSTIFICADO', 4),
  (5, 1, '2026-09-11', 'PRESENTE',    4);

INSERT INTO justificacion (asistencia_id, motivo, evidencia_url, estado) VALUES
  (2, 'Cita médica programada', NULL, 'APROBADA');

INSERT INTO comunicado (titulo, detalle, fecha, tipo, aula_id) VALUES
  ('Reunión de apoderados', 'Aula 5° Primaria · 6:00 p.m.', '2026-09-14', 'COMUNICADO', 1),
  ('Simulacro de sismo',    'Toda la institución',          '2026-09-15', 'COMUNICADO', NULL),
  ('Entrega de libretas',   'Auditorio principal',          '2026-09-16', 'CIRCULAR',   NULL);

INSERT INTO evento (id, titulo, descripcion, fecha, hora_inicio, hora_fin, lugar) VALUES
  (1, 'Feria de Ciencias y Día del Logro 2026',
      'Acompaña a nuestros alumnos en la presentación de sus proyectos más innovadores del año.',
      '2026-10-24', '09:00:00', '13:00:00', 'Patio de Honor y Salón Cultural del Colegio');

INSERT INTO evento_participacion (evento_id, apoderado_id, estado) VALUES
  (1, 1, 'ASISTIRA');

INSERT INTO ruta (id, conductor_id, vehiculo_id, fecha, estado, hora_inicio) VALUES
  (1, 2, 1, '2026-09-13', 'ACTIVA', '2026-09-13 07:00:00');

INSERT INTO ruta_paradero (ruta_id, paradero_id, orden) VALUES
  (1, 2, 1),
  (1, 3, 2),
  (1, 1, 3);

INSERT INTO abordaje (ruta_id, estudiante_id, tipo, estado, hora) VALUES
  (1, 1, 'RECOGIDA', 'ABORDO',    '2026-09-13 07:18:00'),
  (1, 2, 'RECOGIDA', 'PENDIENTE', NULL);

INSERT INTO indicador_recurso (fecha, hojas_no_impresas, emisiones_evitadas_kg, km_ruta) VALUES
  ('2026-09-11', 120, 0.540, 18.30),
  ('2026-09-12', 135, 0.608, 18.30),
  ('2026-09-13', 128, 0.576, 18.30);

INSERT INTO notificacion (usuario_id, titulo, mensaje, tipo) VALUES
  (1, 'Tu hijo abordó el vehículo', 'Julio Zuñiga abordó la Movilidad N°04 a las 07:18.', 'ABORDAJE'),
  (1, 'Inasistencia registrada',    'María Zuñiga figura como falta el 08/09.',           'INASISTENCIA');

-- =====================================================================
--  Sincronizar secuencias tras insertar IDs explícitos
-- =====================================================================
SELECT setval(pg_get_serial_sequence('usuario','id'),    (SELECT MAX(id) FROM usuario));
SELECT setval(pg_get_serial_sequence('aula','id'),       (SELECT MAX(id) FROM aula));
SELECT setval(pg_get_serial_sequence('curso','id'),      (SELECT MAX(id) FROM curso));
SELECT setval(pg_get_serial_sequence('apoderado','id'),  (SELECT MAX(id) FROM apoderado));
SELECT setval(pg_get_serial_sequence('conductor','id'),  (SELECT MAX(id) FROM conductor));
SELECT setval(pg_get_serial_sequence('vehiculo','id'),   (SELECT MAX(id) FROM vehiculo));
SELECT setval(pg_get_serial_sequence('paradero','id'),   (SELECT MAX(id) FROM paradero));
SELECT setval(pg_get_serial_sequence('estudiante','id'), (SELECT MAX(id) FROM estudiante));
SELECT setval(pg_get_serial_sequence('asistencia','id'), (SELECT MAX(id) FROM asistencia));
SELECT setval(pg_get_serial_sequence('evento','id'),     (SELECT MAX(id) FROM evento));
SELECT setval(pg_get_serial_sequence('ruta','id'),       (SELECT MAX(id) FROM ruta));

-- =====================================================================
--  FIN DEL SCRIPT
-- =====================================================================
