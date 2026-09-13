-- =====================================================================
--  Colegio San Agustín — App de Transporte y Asistencia Escolar
--  Modelo de datos relacional (MySQL 8 / InnoDB, utf8mb4)
--  Grupo Exodia — Trabajo de Campo 2
--
--  Roles del sistema: APODERADO (estudiante/apoderado) y CONDUCTOR.
--  Incluye DDL (estructura) + datos de prueba por defecto.
--
--  Nota: la posición del vehículo en tiempo real vive en Firestore
--  (no en esta base relacional); aquí solo se guarda la última conocida.
-- =====================================================================

DROP DATABASE IF EXISTS colegio_san_agustin;
CREATE DATABASE colegio_san_agustin
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE colegio_san_agustin;

-- =====================================================================
--  1. SEGURIDAD / USUARIOS
-- =====================================================================

CREATE TABLE usuario (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    correo          VARCHAR(120)  NOT NULL UNIQUE,
    -- La app NO almacena contraseñas planas (Firebase Auth). Este campo
    -- guarda solo la referencia/hash del proveedor de identidad.
    password_hash   VARCHAR(255)  NULL,
    rol             ENUM('APODERADO','CONDUCTOR','ADMIN') NOT NULL,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    creado_en       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- PIN de validación de dos pasos (2FA)
CREATE TABLE codigo_verificacion (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT          NOT NULL,
    codigo      CHAR(6)      NOT NULL,
    expira_en   DATETIME     NOT NULL,
    usado       BOOLEAN      NOT NULL DEFAULT FALSE,
    creado_en   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_codigo_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
--  2. ESTRUCTURA ACADÉMICA
-- =====================================================================

CREATE TABLE aula (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    grado    VARCHAR(20)  NOT NULL,           -- ej. '5°'
    seccion  VARCHAR(5)   NOT NULL,           -- ej. 'A'
    nivel    ENUM('INICIAL','PRIMARIA','SECUNDARIA') NOT NULL,
    UNIQUE KEY uq_aula (grado, seccion, nivel)
) ENGINE=InnoDB;

CREATE TABLE curso (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =====================================================================
--  3. PERSONAS
-- =====================================================================

CREATE TABLE apoderado (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id           INT          NOT NULL UNIQUE,
    nombre               VARCHAR(120) NOT NULL,
    telefono             VARCHAR(30)  NULL,
    correo               VARCHAR(120) NULL,
    direccion            VARCHAR(160) NULL,
    contacto_emergencia  VARCHAR(30)  NULL,
    CONSTRAINT fk_apoderado_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE conductor (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id           INT          NOT NULL UNIQUE,
    nombre               VARCHAR(120) NOT NULL,
    dni                  VARCHAR(15)  NOT NULL UNIQUE,
    celular              VARCHAR(30)  NULL,
    correo               VARCHAR(120) NULL,
    contacto_emergencia  VARCHAR(30)  NULL,
    licencia             VARCHAR(30)  NOT NULL,
    zona_asignada        VARCHAR(40)  NULL,     -- ej. 'RUTA 2'
    CONSTRAINT fk_conductor_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE vehiculo (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    numero         VARCHAR(30)  NOT NULL,        -- ej. 'Movilidad N°04'
    placa          VARCHAR(10)  NOT NULL UNIQUE,
    capacidad      INT          NOT NULL DEFAULT 15,
    conductor_id   INT          NULL,
    CONSTRAINT fk_vehiculo_conductor FOREIGN KEY (conductor_id)
        REFERENCES conductor(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE paradero (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(80)  NOT NULL,
    direccion  VARCHAR(160) NULL,
    latitud    DECIMAL(10,7) NULL,
    longitud   DECIMAL(10,7) NULL
) ENGINE=InnoDB;

CREATE TABLE estudiante (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombres       VARCHAR(80)  NOT NULL,
    apellidos     VARCHAR(80)  NOT NULL,
    codigo_qr     VARCHAR(40)  NOT NULL UNIQUE,   -- código de abordaje
    foto_url      VARCHAR(255) NULL,
    aula_id       INT          NOT NULL,
    vehiculo_id   INT          NULL,
    paradero_id   INT          NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_estudiante_aula FOREIGN KEY (aula_id)
        REFERENCES aula(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_estudiante_vehiculo FOREIGN KEY (vehiculo_id)
        REFERENCES vehiculo(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_estudiante_paradero FOREIGN KEY (paradero_id)
        REFERENCES paradero(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Un estudiante puede tener uno o más apoderados (M:N).
CREATE TABLE apoderado_estudiante (
    apoderado_id   INT NOT NULL,
    estudiante_id  INT NOT NULL,
    parentesco     ENUM('PADRE','MADRE','TUTOR','OTRO') NOT NULL DEFAULT 'TUTOR',
    PRIMARY KEY (apoderado_id, estudiante_id),
    CONSTRAINT fk_ae_apoderado FOREIGN KEY (apoderado_id)
        REFERENCES apoderado(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ae_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
--  4. ASISTENCIA Y JUSTIFICACIONES
-- =====================================================================

CREATE TABLE asistencia (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id  INT   NOT NULL,
    fecha          DATE  NOT NULL,
    estado         ENUM('PRESENTE','TARDANZA','FALTA','JUSTIFICADO') NOT NULL,
    registrado_por INT   NULL,                 -- usuario que registró (docente/admin)
    sincronizado   BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_asistencia (estudiante_id, fecha),
    CONSTRAINT fk_asistencia_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_asistencia_usuario FOREIGN KEY (registrado_por)
        REFERENCES usuario(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE justificacion (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    asistencia_id INT          NOT NULL UNIQUE,   -- una justificación por falta
    motivo        VARCHAR(255) NOT NULL,
    evidencia_url VARCHAR(255) NULL,
    estado        ENUM('PENDIENTE','APROBADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    creado_en     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_justificacion_asistencia FOREIGN KEY (asistencia_id)
        REFERENCES asistencia(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
--  5. NOTAS
-- =====================================================================

CREATE TABLE nota (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    estudiante_id  INT          NOT NULL,
    curso_id       INT          NOT NULL,
    periodo        VARCHAR(30)  NOT NULL,        -- ej. 'II Bimestre'
    valor          DECIMAL(4,1) NOT NULL,        -- 0.0 .. 20.0
    detalle        VARCHAR(120) NULL,
    UNIQUE KEY uq_nota (estudiante_id, curso_id, periodo),
    CONSTRAINT chk_nota_valor CHECK (valor >= 0 AND valor <= 20),
    CONSTRAINT fk_nota_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_nota_curso FOREIGN KEY (curso_id)
        REFERENCES curso(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
--  6. COMUNICADOS Y EVENTOS
-- =====================================================================

CREATE TABLE comunicado (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    titulo     VARCHAR(120) NOT NULL,
    detalle    VARCHAR(255) NULL,
    fecha      DATE         NOT NULL,
    tipo       ENUM('COMUNICADO','CIRCULAR') NOT NULL DEFAULT 'COMUNICADO',
    aula_id    INT          NULL,               -- NULL = para toda la institución
    creado_en  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comunicado_aula FOREIGN KEY (aula_id)
        REFERENCES aula(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE evento (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    titulo       VARCHAR(120) NOT NULL,
    descripcion  TEXT         NULL,
    fecha        DATE         NOT NULL,
    hora_inicio  TIME         NULL,
    hora_fin     TIME         NULL,
    lugar        VARCHAR(160) NULL,
    imagen_url   VARCHAR(255) NULL,
    creado_en    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Participación del apoderado en un evento (Guardar / Asistir).
CREATE TABLE evento_participacion (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    evento_id     INT NOT NULL,
    apoderado_id  INT NOT NULL,
    estado        ENUM('GUARDADO','ASISTIRA','NO_ASISTIRA') NOT NULL DEFAULT 'GUARDADO',
    creado_en     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_participacion (evento_id, apoderado_id),
    CONSTRAINT fk_part_evento FOREIGN KEY (evento_id)
        REFERENCES evento(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_part_apoderado FOREIGN KEY (apoderado_id)
        REFERENCES apoderado(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
--  7. TRANSPORTE
-- =====================================================================

CREATE TABLE ruta (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    conductor_id INT   NOT NULL,
    vehiculo_id  INT   NOT NULL,
    fecha        DATE  NOT NULL,
    estado       ENUM('INACTIVA','ACTIVA','FINALIZADA') NOT NULL DEFAULT 'INACTIVA',
    hora_inicio  DATETIME NULL,
    hora_fin     DATETIME NULL,
    -- Última posición conocida del vehículo (la de tiempo real vive en Firestore).
    ult_latitud  DECIMAL(10,7) NULL,
    ult_longitud DECIMAL(10,7) NULL,
    ult_actualizacion DATETIME NULL,
    CONSTRAINT fk_ruta_conductor FOREIGN KEY (conductor_id)
        REFERENCES conductor(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ruta_vehiculo FOREIGN KEY (vehiculo_id)
        REFERENCES vehiculo(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ruta_paradero (
    ruta_id      INT NOT NULL,
    paradero_id  INT NOT NULL,
    orden        INT NOT NULL,
    PRIMARY KEY (ruta_id, paradero_id),
    CONSTRAINT fk_rp_ruta FOREIGN KEY (ruta_id)
        REFERENCES ruta(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rp_paradero FOREIGN KEY (paradero_id)
        REFERENCES paradero(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE abordaje (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    ruta_id        INT NOT NULL,
    estudiante_id  INT NOT NULL,
    tipo           ENUM('RECOGIDA','ENTREGA') NOT NULL,
    estado         ENUM('PENDIENTE','ABORDO','ENTREGADO','AUSENTE','NO_CONTACTADO')
                   NOT NULL DEFAULT 'PENDIENTE',
    hora           DATETIME NULL,
    incidencia     VARCHAR(200) NULL,
    sincronizado   BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE KEY uq_abordaje (ruta_id, estudiante_id, tipo),
    CONSTRAINT fk_abordaje_ruta FOREIGN KEY (ruta_id)
        REFERENCES ruta(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_abordaje_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =====================================================================
--  8. INDICADORES ODS 12 Y NOTIFICACIONES
-- =====================================================================

CREATE TABLE indicador_recurso (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    fecha                DATE NOT NULL UNIQUE,
    hojas_no_impresas    INT           NOT NULL DEFAULT 0,
    emisiones_evitadas_kg DECIMAL(8,3) NOT NULL DEFAULT 0,   -- kg CO2e
    km_ruta              DECIMAL(8,2)  NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE notificacion (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT          NOT NULL,
    titulo      VARCHAR(120) NOT NULL,
    mensaje     VARCHAR(255) NULL,
    tipo        ENUM('ABORDAJE','INASISTENCIA','COMUNICADO','EVENTO','GENERAL')
                NOT NULL DEFAULT 'GENERAL',
    leida       BOOLEAN      NOT NULL DEFAULT FALSE,
    creado_en   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Índices de apoyo para consultas frecuentes
CREATE INDEX idx_asistencia_fecha   ON asistencia(fecha);
CREATE INDEX idx_abordaje_estado    ON abordaje(estado);
CREATE INDEX idx_estudiante_vehiculo ON estudiante(vehiculo_id);
CREATE INDEX idx_comunicado_fecha   ON comunicado(fecha);


-- =====================================================================
--  DATOS DE PRUEBA (SEED)
-- =====================================================================

-- Usuarios (credenciales de prueba; la contraseña real la maneja Firebase Auth)
INSERT INTO usuario (id, correo, password_hash, rol) VALUES
  (1, 'apoderado@colegio.edu.pe', NULL, 'APODERADO'),
  (2, 'conductor@colegio.edu.pe', NULL, 'CONDUCTOR'),
  (3, 'carlos.garcia@colegio.edu.pe', NULL, 'CONDUCTOR'),
  (4, 'admin@colegio.edu.pe',     NULL, 'ADMIN');

-- Aulas
INSERT INTO aula (id, grado, seccion, nivel) VALUES
  (1, '5°', 'A', 'PRIMARIA'),
  (2, '1°', 'A', 'PRIMARIA');

-- Cursos
INSERT INTO curso (id, nombre) VALUES
  (1, 'Matemática'),
  (2, 'Comunicación'),
  (3, 'Ciencia y Tecnología'),
  (4, 'Personal Social'),
  (5, 'Arte y Cultura');

-- Apoderado
INSERT INTO apoderado (id, usuario_id, nombre, telefono, correo, direccion, contacto_emergencia) VALUES
  (1, 1, 'Marco Zuñiga', '(+51) 987 654 121', 'marco.zuniga@email.com', 'Av. Principal 128', '987 111 222');

-- Conductores
INSERT INTO conductor (id, usuario_id, nombre, dni, celular, correo, contacto_emergencia, licencia, zona_asignada) VALUES
  (1, 2, 'Rosa Melano',  '78445564', '961541515151', 'rosa.melano@colegio.edu.pe',  '954784548', 'Ad4c548u4d', 'RUTA 2'),
  (2, 3, 'Carlos García','45128796', '999888777',    'carlos.garcia@colegio.edu.pe','954112233', 'C-90887766', 'RUTA 1');

-- Vehículos
INSERT INTO vehiculo (id, numero, placa, capacidad, conductor_id) VALUES
  (1, 'Movilidad N°04', 'ABC-123', 15, 2),
  (2, 'Movilidad N°02', 'XDS-456', 15, 1);

-- Paraderos
INSERT INTO paradero (id, nombre, direccion, latitud, longitud) VALUES
  (1, 'Paradero Av. Principal', 'Av. Principal 128', -12.0463700, -77.0427800),
  (2, 'Paradero Los Álamos',    'Av. Los Álamos 145', -12.0480000, -77.0450000),
  (3, 'Paradero Magnolia',      'Calle Magnolia 128', -12.0500000, -77.0470000);

-- Estudiantes
INSERT INTO estudiante (id, nombres, apellidos, codigo_qr, aula_id, vehiculo_id, paradero_id) VALUES
  (1, 'Julio', 'Zuñiga', 'QR-JULIO-0001', 1, 1, 1),
  (2, 'María', 'Zuñiga', 'QR-MARIA-0002', 2, 1, 1);

-- Vínculo apoderado ↔ estudiante
INSERT INTO apoderado_estudiante (apoderado_id, estudiante_id, parentesco) VALUES
  (1, 1, 'PADRE'),
  (1, 2, 'PADRE');

-- Notas (Julio — II Bimestre)
INSERT INTO nota (estudiante_id, curso_id, periodo, valor, detalle) VALUES
  (1, 1, 'II Bimestre', 18.0, 'Examen bimestral'),
  (1, 2, 'II Bimestre', 17.0, 'Comprensión lectora'),
  (1, 3, 'II Bimestre', 20.0, 'Proyecto de feria'),
  (1, 4, 'II Bimestre', 16.0, 'Trabajo grupal'),
  (1, 5, 'II Bimestre', 19.0, 'Exposición');

-- Asistencia (Julio — muestra de la semana)
INSERT INTO asistencia (id, estudiante_id, fecha, estado, registrado_por) VALUES
  (1, 1, '2026-09-07', 'PRESENTE',    4),
  (2, 1, '2026-09-08', 'FALTA',       4),
  (3, 1, '2026-09-09', 'TARDANZA',    4),
  (4, 1, '2026-09-10', 'JUSTIFICADO', 4),
  (5, 1, '2026-09-11', 'PRESENTE',    4);

-- Justificación para la falta del 08/09
INSERT INTO justificacion (asistencia_id, motivo, evidencia_url, estado) VALUES
  (2, 'Cita médica programada', NULL, 'APROBADA');

-- Comunicados
INSERT INTO comunicado (titulo, detalle, fecha, tipo, aula_id) VALUES
  ('Reunión de apoderados', 'Aula 5° Primaria · 6:00 p.m.', '2026-09-14', 'COMUNICADO', 1),
  ('Simulacro de sismo',    'Toda la institución',          '2026-09-15', 'COMUNICADO', NULL),
  ('Entrega de libretas',   'Auditorio principal',          '2026-09-16', 'CIRCULAR',   NULL);

-- Evento
INSERT INTO evento (id, titulo, descripcion, fecha, hora_inicio, hora_fin, lugar) VALUES
  (1, 'Feria de Ciencias y Día del Logro 2026',
      'Acompaña a nuestros alumnos en la presentación de sus proyectos más innovadores del año.',
      '2026-10-24', '09:00:00', '13:00:00', 'Patio de Honor y Salón Cultural del Colegio');

INSERT INTO evento_participacion (evento_id, apoderado_id, estado) VALUES
  (1, 1, 'ASISTIRA');

-- Ruta activa de hoy (Carlos García / Movilidad N°04)
INSERT INTO ruta (id, conductor_id, vehiculo_id, fecha, estado, hora_inicio) VALUES
  (1, 2, 1, '2026-09-13', 'ACTIVA', '2026-09-13 07:00:00');

INSERT INTO ruta_paradero (ruta_id, paradero_id, orden) VALUES
  (1, 2, 1),
  (1, 3, 2),
  (1, 1, 3);

-- Abordajes de la ruta
INSERT INTO abordaje (ruta_id, estudiante_id, tipo, estado, hora) VALUES
  (1, 1, 'RECOGIDA', 'ABORDO',    '2026-09-13 07:18:00'),
  (1, 2, 'RECOGIDA', 'PENDIENTE', NULL);

-- Indicadores ODS 12
INSERT INTO indicador_recurso (fecha, hojas_no_impresas, emisiones_evitadas_kg, km_ruta) VALUES
  ('2026-09-11', 120, 0.540, 18.30),
  ('2026-09-12', 135, 0.608, 18.30),
  ('2026-09-13', 128, 0.576, 18.30);

-- Notificaciones de ejemplo
INSERT INTO notificacion (usuario_id, titulo, mensaje, tipo) VALUES
  (1, 'Tu hijo abordó el vehículo', 'Julio Zuñiga abordó la Movilidad N°04 a las 07:18.', 'ABORDAJE'),
  (1, 'Inasistencia registrada',    'María Zuñiga figura como falta el 08/09.',           'INASISTENCIA');

-- =====================================================================
--  FIN DEL SCRIPT
-- =====================================================================
