# Gym Management API v1.0.0

## 📚 Descripción
API RESTful para la gestión integral de un gimnasio, incluyendo miembros, planes de membresía, pagos y promociones. Esta documentación cubre la versión 1 de la API.

## 🔗 Documentación Interactiva
La documentación interactiva de la API está disponible en:
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI (JSON)**: http://localhost:8080/api/v1/v3/api-docs

## 🌐 Base URL
Todas las rutas de la API están bajo el prefijo: `/api/v1`

## 🔐 Autenticación y Seguridad JWT (Actualizado)

La API implementa autenticación y autorización basada en JWT usando la librería Auth0 Java JWT (compatible con Java 17/21).

- **Login y Registro:**
  - `POST /api/v1/auth/register` — Registro de usuario (público)
  - `POST /api/v1/auth/login` — Login de usuario (público)
  - Ambos retornan un JWT en la respuesta.

- **Uso del token JWT:**
  - Para acceder a rutas protegidas, añade el header:
    ```
    Authorization: Bearer TU_TOKEN_AQUI
    ```

- **Gestión de roles:**
  - El JWT incluye el rol del usuario (`USER` o `ADMIN`).
  - Los endpoints usan `@PreAuthorize` para restringir acceso según el rol:
    - `@PreAuthorize("hasRole('ADMIN')")` — Solo administradores.
    - `@PreAuthorize("hasAnyRole('USER','ADMIN')")` — Usuarios autenticados.
    - Rutas públicas no requieren token.

## 🛡️ Acceso por Roles a Endpoints

| Endpoint                            | Público | USER | ADMIN |
|--------------------------------------|:-------:|:----:|:-----:|
| POST /auth/register, /auth/login     |   ✔     |  ✔   |   ✔   |
| GET  /membership-plans, /promotions |   ✔     |  ✔   |   ✔   |
| POST/PUT/DELETE /membership-plans   |         |      |   ✔   |
| GET  /gym-members                   |         |  ✔   |   ✔   |
| POST/PUT/DELETE /gym-members        |         |      |   ✔   |
| GET  /payments, /membership-records |         |  ✔   |   ✔   |
| POST/PUT/DELETE /payments           |         |      |   ✔   |
| POST/DELETE /membership-records     |         |      |   ✔   |
| GET  /reports                       |         |      |   ✔   |

- Si el usuario no tiene el rol requerido, recibe HTTP 403 Forbidden.

## 🚀 Ejemplo de autenticación desde el Frontend

```js
// Login
fetch('/api/v1/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'user', password: 'pass' })
})
  .then(res => res.json())
  .then(data => {
    localStorage.setItem('token', data.token);
  });

// Acceso a endpoint protegido
fetch('/api/v1/gym-members', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
```

## 📊 Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - La solicitud se completó con éxito |
| 201 | Creado - Recurso creado exitosamente |
| 400 | Solicitud incorrecta - La solicitud no es válida |
| 401 | No autorizado - Se requiere autenticación |
| 403 | Prohibido - No tiene permiso para acceder al recurso |
| 404 | No encontrado - El recurso solicitado no existe |
| 500 | Error del servidor - Error interno del servidor |

## 📍 Endpoints Principales

### Miembros (Gym Members)
- `GET /api/v1/gym-members` - Obtener todos los miembros
- `GET /api/v1/gym-members/{id}` - Obtener un miembro por ID
- `POST /api/v1/gym-members` - Crear un nuevo miembro
- `PUT /api/v1/gym-members/{id}` - Actualizar un miembro existente
- `DELETE /api/v1/gym-members/{id}` - Eliminar un miembro

### Reportes PDF de Miembro
- `GET /api/v1/gym-members-pdf/members/{id}/report` - Descargar reporte PDF de un miembro

### Planes de Membresía (Membership Plans)
- `GET /api/v1/membership-plans` - Obtener todos los planes
- `GET /api/v1/membership-plans/{id}` - Obtener un plan por ID
- `POST /api/v1/membership-plans` - Crear un nuevo plan
- `PUT /api/v1/membership-plans/{id}` - Actualizar un plan
- `DELETE /api/v1/membership-plans/{id}` - Eliminar un plan

### Registros de Membresía (Membership Records)
- `GET /api/v1/membership-records` - Obtener todos los registros
- `GET /api/v1/membership-records/{gymMemberId}` - Obtener registros de un miembro
- `POST /api/v1/membership-records` - Crear un nuevo registro
- `DELETE /api/v1/membership-records/{id}` - Eliminar un registro

### Promociones (Promotions)
- `GET /api/v1/promotions` - Obtener todas las promociones
- `GET /api/v1/promotions/{id}` - Obtener una promoción por ID
- `POST /api/v1/promotions` - Crear una nueva promoción
- `PUT /api/v1/promotions/{id}` - Actualizar una promoción
- `DELETE /api/v1/promotions/{id}` - Eliminar una promoción

### Pagos (Payments)
- `GET /api/v1/payments` - Obtener todos los pagos
- `GET /api/v1/payments/{id}` - Obtener un pago por ID
- `POST /api/v1/payments` - Crear un nuevo pago
- `PUT /api/v1/payments/{id}` - Actualizar un pago
- `DELETE /api/v1/payments/{id}` - Eliminar un pago

### Reportes (Reports)
- `GET /api/v1/reports/retention` - Obtener reporte de retención
- `GET /api/v1/reports/retention/pdf` - Descargar reporte de retención en PDF

## 📝 Ejemplos de Solicitudes

### 1. Crear un nuevo pago

**URL**: `POST /api/v1/payments`

**Cuerpo de la solicitud**:
```json
{
  "amount": 100.00,
  "paymentDate": "2025-06-11",
  "paymentMethod": "TARJETA_CREDITO",
  "status": "PENDIENTE",
  "gymMemberId": "32e34ad4-d55a-4211-9fe0-bfcb24ed008e"
}
```

**Respuesta exitosa (201 Created)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 100.00,
  "paymentDate": "2025-06-11",
  "paymentMethod": "TARJETA_CREDITO",
  "status": "PENDIENTE",
  "gymMemberId": "32e34ad4-d55a-4211-9fe0-bfcb24ed008e"
}
```

### 2. Obtener un pago por ID

**URL**: `GET /api/v1/payments/550e8400-e29b-41d4-a716-446655440000`

**Respuesta exitosa (200 OK)**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 100.00,
  "paymentDate": "2025-06-11",
  "paymentMethod": "TARJETA_CREDITO",
  "status": "COMPLETADO",
  "gymMemberId": "32e34ad4-d55a-4211-9fe0-bfcb24ed008e"
}
```

## 📌 Manejo de Errores

La API devuelve respuestas de error estandarizadas en formato JSON. Ejemplo:

```json
{
  "timestamp": "2025-06-11T18:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Payment not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/payments/550e8400-e29b-41d4-a716-446655440000"
}
```

## 📦 Instalación

1. Clonar el repositorio
2. Configurar la base de datos en `application.yml`
3. Ejecutar la aplicación con Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🛠 Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.2
- Spring Data JPA
- PostgreSQL
- SpringDoc OpenAPI 2.3.0
- Maven
- Auth0 Java JWT 4.4.0
- Spring Security
- BCrypt

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🚀 Futuras Mejoras y Casos de Uso

### Mejoras Futuras Sugeridas
- **Notificaciones**: Envío de recordatorios de pago y promociones por email/SMS.
- **Dashboard Analítico**: Panel para métricas de retención, ingresos y asistencia.
- **Integración con Apps de Fitness**: Permitir importar datos de apps externas.
- **Gestión de Clases y Horarios**: Reservas y control de asistencia a clases.
- **Pagos en Línea**: Integrar pasarelas de pago para cobros automáticos.
- **Soporte Multisucursal**: Gestionar múltiples gimnasios desde una sola plataforma.
- **Historial de Cambios**: Auditoría de acciones y cambios en registros.

### Casos de Uso Positivos para la Empresa
- **Mejor Experiencia de Usuario**: Automatización de procesos y autoservicio para clientes.
- **Retención de Clientes**: Seguimiento y análisis de bajas para acciones preventivas.
- **Optimización de Ingresos**: Promociones segmentadas y cobros automatizados.
- **Toma de Decisiones Basada en Datos**: Informes y dashboards para decisiones estratégicas.
- **Expansión Fácil**: Base sólida para escalar a nuevas sucursales o franquicias.

![pdfGym](https://github.com/user-attachments/assets/93900cae-e944-4214-88bb-5fc352af53e4)

![report](https://github.com/user-attachments/assets/939ee3c8-08a7-40ed-b2d7-215c756d56ab)

---

## Notas
- Todos los UUIDs son generados automáticamente por la base de datos.
- Los pagos y registros de membresía están asociados a un miembro y su plan de membresía.
- Las fechas siguen el formato `YYYY-MM-DD`.
- Se recomienda validar los datos antes de enviarlos a la API.
