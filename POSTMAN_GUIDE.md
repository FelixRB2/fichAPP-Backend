# Guía de Endpoints para Postman - fichAPP Backend

Esta guía detalla todos los endpoints disponibles en el backend de **fichAPP**, configurados para ejecutarse en el puerto **8085** (`http://localhost:8085`).

> [!NOTE]
> Actualmente, la configuración de seguridad en `SecurityConfig.java` tiene permitidos todos los accesos sin token obligatorio (`.permitAll()`), por lo que puedes probar la mayoría de los endpoints directamente sin necesidad de añadir cabeceras de autorización. Si en el futuro se activa el filtro JWT, deberás añadir el encabezado `Authorization: Bearer <token>` obtenido en el Login.

---

## Índice de Controladores
1. [Autenticación (AuthController)](#1-autenticación-authcontroller)
2. [Usuarios (UsuarioController)](#2-usuarios-usuariocontroller)
3. [Fichajes (FichajeController)](#3-fichajes-fichajecontroller)
4. [Horarios (HorarioController)](#4-horarios-horariocontroller)
5. [Solicitudes (SolicitudController)](#5-solicitudes-solicitudcontroller)

---

## 1. Autenticación (`AuthController`)
Ruta base: `http://localhost:8085/api/auth`

### 1.1. Iniciar Sesión (Login)
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/auth/login`
* **Descripción:** Permite a un usuario autenticarse y recibir su Token JWT, Rol, Nombre e ID de usuario.
* **Cuerpo (JSON):**
  ```json
  {
    "email": "admin@test.com",
    "password": "contrasena_secreta"
  }
  ```

### 1.2. Registrar Nuevo Usuario (Registro rápido)
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/auth/register`
* **Descripción:** Registra un nuevo usuario en la base de datos (por defecto con rol `Trabajador` si no se especifica).
* **Cuerpo (JSON):**
  ```json
  {
    "nombreRol": "Trabajador", 
    "nombre": "Carlos",
    "apellido1": "Gómez",
    "apellido2": "Pérez",
    "email": "carlos.gomez@test.com",
    "puesto": "Desarrollador Junior",
    "horasSemanales": 40.0,
    "contrasena": "carlos123"
  }
  ```
  *(Nota: `nombreRol` puede ser `"Trabajador"` o `"Administrador"`)*

---

## 2. Usuarios (`UsuarioController`)
Ruta base: `http://localhost:8085/api/usuarios`

### 2.1. Crear Usuario (Administración)
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/usuarios`
* **Descripción:** Registra un usuario completo desde el panel de administración.
* **Cuerpo (JSON):**
  ```json
  {
    "nombreRol": "Trabajador",
    "nombre": "Ana",
    "apellido1": "Martínez",
    "apellido2": "Sanz",
    "correo": "ana.martinez@test.com",
    "puesto": "Diseñadora UX",
    "horasSemanales": 35.0,
    "contrasena": "ana123"
  }
  ```

### 2.2. Listar Todos los Usuarios
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/usuarios`
* **Descripción:** Devuelve una lista de todos los usuarios registrados.

### 2.3. Obtener Usuario por ID
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/usuarios/{id}`
* **Descripción:** Devuelve la información de un usuario específico.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del usuario (ej. `a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d`)

### 2.4. Actualizar Usuario Completo
* **Método:** `PUT`
* **URL:** `http://localhost:8085/api/usuarios/{id}`
* **Descripción:** Actualiza todos los campos de un usuario existente.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del usuario
* **Cuerpo (JSON):**
  ```json
  {
    "nombreRol": "Trabajador",
    "nombre": "Ana María",
    "apellido1": "Martínez",
    "apellido2": "Sánchez",
    "correo": "anamaria.martinez@test.com",
    "puesto": "Lead UX Designer",
    "horasSemanales": 40.0,
    "contrasena": "nuevaContrasenaOpcional",
    "activo": true
  }
  ```

### 2.5. Actualizar Estado del Usuario (Activar/Suspender)
* **Método:** `PATCH`
* **URL:** `http://localhost:8085/api/usuarios/{id}/estado`
* **Descripción:** Activa o suspende rápidamente la cuenta de un usuario.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del usuario
* **Cuerpo (JSON):**
  ```json
  {
    "activo": false
  }
  ```

### 2.6. Eliminar Usuario
* **Método:** `DELETE`
* **URL:** `http://localhost:8085/api/usuarios/{id}`
* **Descripción:** Elimina definitivamente a un usuario del sistema.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del usuario

---

## 3. Fichajes (`FichajeController`)
Ruta base: `http://localhost:8085/api/fichajes`

### 3.1. Registrar Entrada (Fichar Entrada)
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/fichajes`
* **Descripción:** Crea un registro de fichaje capturando la hora actual de entrada y opcionalmente coordenadas GPS de geolocalización.
* **Cuerpo (JSON):**
  ```json
  {
    "idUsuario": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "comentario": "Fichando desde la oficina principal",
    "latitud": 40.416775,
    "longitud": -3.703790
  }
  ```

### 3.2. Registrar Salida (Fichar Salida)
* **Método:** `PUT`
* **URL:** `http://localhost:8085/api/fichajes/{id}/salida`
* **Descripción:** Actualiza un fichaje abierto registrando la hora de salida actual y las coordenadas GPS.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del fichaje activo
* **Cuerpo (JSON):**
  ```json
  {
    "latitud": 40.416800,
    "longitud": -3.703810
  }
  ```

### 3.3. Obtener Fichajes por Usuario
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/fichajes/usuario/{idUsuario}`
* **Descripción:** Devuelve todo el historial de fichajes de un usuario.
* **Parámetro de Ruta (Path):** `{idUsuario}` -> UUID del usuario

### 3.4. Obtener Datos del Dashboard
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/fichajes/dashboard/{idUsuario}`
* **Descripción:** Obtiene los datos del usuario listos para cargar su vista del dashboard (fichaje actual, estadísticas de horas, etc.).
* **Parámetro de Ruta (Path):** `{idUsuario}` -> UUID del usuario

### 3.5. Solicitar Corrección Directa de un Fichaje
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/fichajes/{id}/solicitar-correccion`
* **Descripción:** El trabajador solicita modificar las horas de un fichaje previo por haberse equivocado o no haber fichado.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del fichaje
* **Cuerpo (JSON):**
  ```json
  {
    "horaEntrada": "09:00:00",
    "horaSalida": "18:00:00",
    "comentario": "Olvidé fichar la salida a las 18:00"
  }
  ```

### 3.6. Resolver Corrección Directa (Administrador)
* **Método:** `PUT`
* **URL:** `http://localhost:8085/api/fichajes/{id}/resolver-correccion?aprobado=true`
* **Descripción:** El administrador aprueba o rechaza una solicitud de corrección directa de fichaje.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del fichaje
* **Parámetro de Consulta (Query):** `aprobado` (puede ser `true` o `false`)

### 3.7. Obtener Fichajes Pendientes de Revisión (Administrador)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/fichajes/pendientes-revision`
* **Descripción:** Lista todos los fichajes que tienen una solicitud de corrección directa pendiente.

### 3.8. Modificar Fichaje Directamente (Forzado / Admin)
* **Método:** `PUT`
* **URL:** `http://localhost:8085/api/fichajes/{id}`
* **Descripción:** Actualiza directamente un fichaje sin pasar por flujo de solicitudes de aprobación.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del fichaje
* **Cuerpo (JSON):**
  ```json
  {
    "horaEntrada": "08:30:00",
    "horaSalida": "17:30:00",
    "comentario": "Corregido manualmente por administración"
  }
  ```

---

## 4. Horarios (`HorarioController`)
Ruta base: `http://localhost:8085/api/horarios`

### 4.1. Asignar Horario a un Usuario
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/horarios`
* **Descripción:** Asigna un turno u horario recurrente a un usuario para ciertos días y un rango de fechas.
* **Cuerpo (JSON):**
  ```json
  {
    "idUsuario": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "nombre": "Turno Mañana",
    "horaEntrada": "08:00:00",
    "horaSalida": "16:00:00",
    "diasLaborables": "lunes,martes,miercoles,jueves,viernes",
    "fechaInicio": "2026-06-01",
    "fechaFin": "2026-12-31"
  }
  ```
  *(Nota: `fechaInicio` es opcional, si no se envía se usa la fecha de hoy. `fechaFin` también es opcional si el horario es indefinido)*

### 4.2. Listar Todos los Horarios de un Usuario
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/horarios/usuario/{idUsuario}`
* **Descripción:** Obtiene todos los horarios históricos y programados de un usuario.
* **Parámetro de Ruta (Path):** `{idUsuario}` -> UUID del usuario

### 4.3. Obtener Horarios Vigentes de un Usuario
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/horarios/usuario/{idUsuario}/vigente`
* **Descripción:** Obtiene los horarios que están activos en la fecha actual para el usuario.
* **Parámetro de Ruta (Path):** `{idUsuario}` -> UUID del usuario

### 4.4. Eliminar Horario
* **Método:** `DELETE`
* **URL:** `http://localhost:8085/api/horarios/{id}`
* **Descripción:** Elimina un horario asignado.
* **Parámetro de Ruta (Path):** `{id}` -> UUID del horario asignado

---

## 5. Solicitudes (`SolicitudController`)
Ruta base: `http://localhost:8085/api/solicitudes`

### 5.1. Listar Todas las Solicitudes (Historial General / Admin)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/solicitudes`
* **Descripción:** Lista todas las solicitudes de cualquier tipo registradas en el sistema.

### 5.2. Listar Solicitudes Pendientes (Admin)
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/solicitudes/pendientes`
* **Descripción:** Lista únicamente las solicitudes pendientes de resolución.

### 5.3. Obtener Solicitudes de un Usuario
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/solicitudes/usuario/{idUsuario}`
* **Descripción:** Lista las solicitudes hechas por un usuario en particular.
* **Parámetro de Ruta (Path):** `{idUsuario}` -> UUID del usuario

### 5.4. Crear Solicitud de Corrección de Horas
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/solicitudes/correccion`
* **Descripción:** El trabajador crea una solicitud formal para corregir un fichaje erróneo.
* **Cuerpo (JSON):**
  ```json
  {
    "idUsuario": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "idFichaje": "4ab92d11-c918-477a-b219-c09a32cbf81c",
    "nuevaHoraEntrada": "09:00:00",
    "nuevaHoraSalida": "18:00:00",
    "comentario": "Olvidé marcar salida y el sistema guardó hora incorrecta"
  }
  ```

### 5.5. Crear Solicitud de Ausencia (Vacaciones, Enfermedad, etc.)
* **Método:** `POST`
* **URL:** `http://localhost:8085/api/solicitudes/ausencia`
* **Descripción:** El trabajador solicita una ausencia cargando opcionalmente un archivo justificante (PDF o imagen).
* **Content-Type:** `multipart/form-data`
* **Parámetros del Formulario (Body -> form-data):**
  * `idUsuario`: `3fa85f64-5717-4562-b3fc-2c963f66afa6` (UUID)
  * `motivo`: `Médico` (Texto, ej. "Vacaciones", "Enfermedad", "Asuntos Propios")
  * `fechaInicio`: `2026-06-10` (Fecha)
  * `fechaFin`: `2026-06-12` (Fecha)
  * `comentario`: `Cita con el especialista en hospital` (Texto, opcional)
  * `archivo`: *(Seleccionar tipo File, opcional - Carga de archivo justificante)*

### 5.6. Resolver Solicitud (Aprobar o Rechazar Ausencia/Corrección)
* **Método:** `PUT`
* **URL:** `http://localhost:8085/api/solicitudes/{id}/resolver?aprobado=true&idRevisor=3fa85f64-5717-4562-b3fc-2c963f66afa6`
* **Descripción:** Un administrador resuelve una solicitud de ausencia o corrección.
* **Parámetro de Ruta (Path):** `{id}` -> UUID de la solicitud
* **Parámetros de Consulta (Query Params):**
  * `aprobado`: `true` o `false`
  * `idRevisor`: UUID del administrador que resuelve la solicitud

### 5.7. Descargar/Visualizar Archivo Justificante de Ausencia
* **Método:** `GET`
* **URL:** `http://localhost:8085/api/solicitudes/archivo/{filename}`
* **Descripción:** Descarga o visualiza en línea el documento justificante cargado para la ausencia.
* **Parámetro de Ruta (Path):** `{filename}` -> Nombre del archivo guardado en el servidor (ej: `justificante_123.pdf`)

---

## 💡 Consejos útiles para Postman

1. **Crear una Variable de Entorno:**
   Crea una variable llamada `{{base_url}}` con el valor `http://localhost:8085/api` para poder escribir tus URLs de forma dinámica:
   * Ejemplo: `{{base_url}}/auth/login`
2. **Pruebas Rápidas:**
   Puedes copiar directamente los cuerpos JSON descritos arriba y pegarlos en la pestaña **Body -> raw -> JSON** de Postman.
3. **Cargar Archivos en Postman (para la Solicitud de Ausencia):**
   * En Postman, ve a la pestaña **Body** de la petición.
   * Selecciona la opción **form-data**.
   * Escribe las claves: `idUsuario`, `motivo`, `fechaInicio`, `fechaFin`, `comentario` y `archivo`.
   * En la fila de `archivo`, pasa el ratón sobre el campo de la clave y cambia el desplegable que dice `Text` a `File`. Esto te permitirá seleccionar un archivo real de tu ordenador para enviarlo.
