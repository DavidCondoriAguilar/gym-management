# 🏋️‍♂️ Gym Management System v1.0.0

## 📚 Descripción
Sistema integral de gestión para gimnasios que permite administrar miembros, planes de membresía, pagos y promociones. Esta documentación cubre la versión 1 de la API RESTful.

## ✨ Características Principales
- Gestión completa de miembros del gimnasio
- Administración de planes de membresía personalizables
- Sistema de pagos con soporte para diferentes métodos de pago
- Promociones y descuentos especiales
- Generación de reportes y estadísticas
- Autenticación segura con JWT
- Documentación interactiva con Swagger

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

### 🔹 Autenticación
- `POST /api/v1/auth/register` - Registrar un nuevo usuario
- `POST /api/v1/auth/login` - Iniciar sesión y obtener token JWT

### 👥 Miembros (Gym Members)
- `GET /api/v1/gym-members` - Obtener todos los miembros
- `GET /api/v1/gym-members/{id}` - Obtener un miembro por ID
- `POST /api/v1/gym-members` - Crear un nuevo miembro
- `PUT /api/v1/gym-members/{id}` - Actualizar un miembro existente
- `DELETE /api/v1/gym-members/{id}` - Eliminar un miembro
- `GET /api/v1/gym-members/{id}/payments` - Obtener historial de pagos de un miembro
- `GET /api/v1/gym-members/status` - Verificar estado de membresía de un miembro

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

### 💳 Pagos (Payments)
- `GET /api/v1/payments` - Obtener todos los pagos (requiere autenticación)
- `GET /api/v1/payments/{id}` - Obtener un pago por ID
- `POST /api/v1/payments` - Crear un nuevo pago
- `PUT /api/v1/payments/{id}` - Actualizar un pago
- `DELETE /api/v1/payments/{id}` - Eliminar un pago

#### Detalles de la funcionalidad de pagos:
- **Detección de pagos duplicados**: El sistema evita pagos duplicados para el mismo miembro, con el mismo monto y en la misma fecha.
- **Métodos de pago soportados**: EFECTIVO, TARJETA_CREDITO, TRANSFERENCIA, OTRO
- **Estados de pago**: PENDIENTE, COMPLETADO, RECHAZADO, CANCELADO
- **Validaciones**:
  - El monto debe ser mayor a cero
  - La fecha de pago no puede ser futura
  - Se requiere un miembro válido
  - Se requiere un método de pago válido

### Reportes (Reports)
- `GET /api/v1/reports/retention` - Obtener reporte de retención
- `GET /api/v1/reports/retention/pdf` - Descargar reporte de retención en PDF

## 🚀 Guía Rápida de Uso

### 1. Configuración Inicial

1. **Clonar el repositorio**
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd gym-app
   ```

2. **Configurar la base de datos**
   - Editar `src/main/resources/application.yml` con tus credenciales de base de datos

3. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```
   La aplicación estará disponible en: http://localhost:8080

### 2. Autenticación

1. **Registrar un nuevo usuario**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/register \
   -H "Content-Type: application/json" \
   -d '{"username":"admin","password":"password","email":"admin@example.com"}'
   ```

2. **Iniciar sesión**
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/login \
   -H "Content-Type: application/json" \
   -d '{"username":"admin","password":"password"}'
   ```
   Guarda el token JWT devuelto para autenticar las siguientes peticiones.

## 📝 Ejemplos de Uso

### 1. Gestión de Pagos

#### Crear un nuevo pago

**URL**: `POST /api/v1/payments`

**Headers**:
```
Authorization: Bearer [TU_TOKEN_JWT]
Content-Type: application/json
```

**Cuerpo de la solicitud**:
```json
{
  "amount": 150.00,
  "paymentDate": "2025-08-12",
  "method": "EFECTIVO",
  "status": "COMPLETADO",
  "memberId": "32e34ad4-d55a-4211-9fe0-bfcb24ed008e",
  "promotionId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Respuesta exitosa (201 Created)**:
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "amount": 150.00,
  "paymentDate": "2025-08-12",
  "method": "EFECTIVO",
  "status": "COMPLETADO",
  "memberId": "32e34ad4-d55a-4211-9fe0-bfcb24ed008e",
  "promotionId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2025-08-12T10:30:00Z"
}
```

#### Evitar pagos duplicados
El sistema detecta automáticamente pagos duplicados (mismo miembro, mismo monto y misma fecha) y devuelve un error 400:

```json
{
  "timestamp": "2025-08-12T10:35:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Ya existe un pago idéntico para este miembro en la misma fecha",
  "path": "/api/v1/payments"
}
```

### 2. Consultar Estado de Membresía

**URL**: `GET /api/v1/gym-members/status?memberId=32e34ad4-d55a-4211-9fe0-bfcb24ed008e`

**Respuesta exitosa (200 OK)**:
```json
{
  "memberId": "32e34ad4-d55a-4211-9fe0-bfcb24ed008e",
  "memberName": "Juan Pérez",
  "membershipPlan": "Plan Mensual",
  "status": "ACTIVO",
  "startDate": "2025-07-12",
  "endDate": "2025-08-12",
  "totalPaid": 150.00,
  "totalWithDiscount": 150.00,
  "discountPercentage": 0.0,
  "remainingBalance": 0.00,
  "fullyPaid": true
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

## 🛠 Tecnologías Utilizadas

- **Backend**:
  - Java 17
  - Spring Boot 3.2.2
  - Spring Security
  - Spring Data JPA
  - Hibernate
  - Maven
  - JWT para autenticación

- **Base de Datos**:
  - MySQL/PostgreSQL (configurable)
  - Flyway para migraciones

- **Herramientas de Desarrollo**:
  - Lombok
  - MapStruct
  - Swagger/OpenAPI
  - JUnit 5
  - Mockito

## 📦 Instalación y Despliegue

### Requisitos Previos
- Java 17 o superior
- Maven 3.6.3 o superior
- MySQL 8.0+ o PostgreSQL 13+

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd gym-app
   ```

2. **Configurar la base de datos**
   - Crear una base de datos en tu servidor MySQL/PostgreSQL
   - Configurar las credenciales en `src/main/resources/application.yml`

3. **Construir la aplicación**
   ```bash
   ./mvnw clean install
   ```

4. **Ejecutar la aplicación**
   ```bash
   java -jar target/gym-app-0.0.1-SNAPSHOT.jar
   ```

5. **Acceder a la documentación**
   - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
   - OpenAPI JSON: http://localhost:8080/api/v1/v3/api-docs

## 📞 Soporte

Para reportar problemas o solicitar características, por favor abra un issue en el repositorio del proyecto.

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

<div align="center">
  <p>Desarrollado con ❤️ para gimnasios que buscan optimizar su gestión</p>
  <p>© 2025 Gym Management System</p>
</div>
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


---

## 🅰️ Guía Angular: Consumir la API como un profesional (Guards, Interceptors, Roles)

Esta guía te muestra, paso a paso, cómo consumir esta API desde un frontend Angular con buenas prácticas de seguridad, manejo de errores y control de roles.

Prerrequisitos:
- Angular 16+ (o 17)
- Node 18+

### 1) Crear proyecto y dependencias

```bash
ng new gym-frontend --routing --style=scss
cd gym-frontend
npm install jwt-decode
```

### 2) Environments

`src/environments/environment.ts` (desarrollo):
```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

`src/environments/environment.prod.ts` (producción):
```ts
export const environment = {
  production: true,
  apiUrl: 'https://TU_DOMINIO/api/v1'
};
```

### 3) Modelos de Auth y Miembro

`src/app/core/models/auth.models.ts`:
```ts
export interface AuthResponse {
  token: string;
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  email: string; // según backend, login usa email
  password: string;
}
```

`src/app/core/models/gym-member.models.ts`:
```ts
export interface GymMemberResponseDTO {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  // otros campos si existen
}

export interface GymMemberRequestDTO {
  firstName: string;
  lastName: string;
  email: string;
}
```

### 4) AuthService (login, logout, estado y rol)

`src/app/core/services/auth.service.ts`:
```ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest } from '../models/auth.models';
import { environment } from '../../../environments/environment';
import jwtDecode from 'jwt-decode';

interface JwtPayload { sub: string; role: 'USER' | 'ADMIN'; exp: number; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROLE_KEY = 'auth_role';

  private isAuth$ = new BehaviorSubject<boolean>(this.hasValidToken());
  private role$ = new BehaviorSubject<'USER' | 'ADMIN' | null>(this.getRole());

  constructor(private http: HttpClient) {}

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, req).pipe(
      tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.ROLE_KEY, res.role);
        this.isAuth$.next(true);
        this.role$.next(res.role);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.ROLE_KEY);
    this.isAuth$.next(false);
    this.role$.next(null);
  }

  get token(): string | null { return localStorage.getItem(this.TOKEN_KEY); }
  getRole(): 'USER' | 'ADMIN' | null { return localStorage.getItem(this.ROLE_KEY) as any; }

  isAuthenticated$(): Observable<boolean> { return this.isAuth$.asObservable(); }
  roleChanges$(): Observable<'USER' | 'ADMIN' | null> { return this.role$.asObservable(); }

  hasValidToken(): boolean {
    const t = this.token; if (!t) return false;
    try {
      const payload = jwtDecode<JwtPayload>(t);
      return payload.exp * 1000 > Date.now();
    } catch { return false; }
  }
}
```

Nota: El backend retorna `{ token, role }` según AuthServiceImpl, y el JWT contiene claim `role`.

### 5) Interceptor HTTP (Authorization y errores)

`src/app/core/interceptors/auth.interceptor.ts`:
```ts
import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.token;
    const cloned = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

    return next.handle(cloned).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 401) {
          this.auth.logout();
          // Redirigir a login si usas router
        }
        return throwError(() => err);
      })
    );
  }
}
```

Registrar el interceptor en `app.module.ts`:
```ts
providers: [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
]
```

### 6) Guards de Autenticación y Rol

`src/app/core/guards/auth.guard.ts`:
```ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}
  canActivate(): boolean | UrlTree {
    return this.auth.hasValidToken() ? true : this.router.parseUrl('/login');
  }
}
```

`src/app/core/guards/role.guard.ts`:
```ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}
  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const expected: ('USER' | 'ADMIN')[] = route.data['roles'] ?? [];
    const role = this.auth.getRole();
    if (!this.auth.hasValidToken()) return this.router.parseUrl('/login');
    return expected.length === 0 || (role && expected.includes(role)) ? true : this.router.parseUrl('/forbidden');
  }
}
```

### 7) Rutas protegidas (routing)

`src/app/app-routing.module.ts`:
```ts
const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
  { path: 'forbidden', loadComponent: () => import('./shared/forbidden.component').then(m => m.ForbiddenComponent) },

  { path: 'members', canActivate: [AuthGuard], data: { roles: ['USER','ADMIN'] },
    loadChildren: () => import('./features/members/members.routes').then(m => m.MEMBERS_ROUTES) },

  { path: '', pathMatch: 'full', redirectTo: 'members' },
  { path: '**', redirectTo: '' }
];
```

En rutas de `members` puedes restringir acciones admin con RoleGuard:
```ts
export const MEMBERS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./list/members-list.component').then(m => m.MembersListComponent), canActivate: [RoleGuard], data: { roles: ['USER','ADMIN'] } },
  { path: 'create', loadComponent: () => import('./form/member-form.component').then(m => m.MemberFormComponent), canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
  { path: ':id/edit', loadComponent: () => import('./form/member-form.component').then(m => m.MemberFormComponent), canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
];
```

### 8) Servicio Angular para Gym Members

`src/app/core/services/gym-member.service.ts`:
```ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GymMemberRequestDTO, GymMemberResponseDTO } from '../models/gym-member.models';

@Injectable({ providedIn: 'root' })
export class GymMemberService {
  private readonly base = `${environment.apiUrl}/gym-members`;
  constructor(private http: HttpClient) {}

  getAll(): Observable<GymMemberResponseDTO[]> { return this.http.get<GymMemberResponseDTO[]>(this.base); }
  getById(id: string): Observable<GymMemberResponseDTO> { return this.http.get<GymMemberResponseDTO>(`${this.base}/${id}`); }
  create(payload: GymMemberRequestDTO): Observable<GymMemberResponseDTO> { return this.http.post<GymMemberResponseDTO>(this.base, payload); }
  update(id: string, payload: GymMemberRequestDTO): Observable<GymMemberResponseDTO> { return this.http.put<GymMemberResponseDTO>(`${this.base}/${id}`, payload); }
  delete(id: string): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
}
```

### 9) Componentes de ejemplo

Listado (USER/ADMIN):
```ts
@Component({ selector: 'app-members-list', template: `
  <h1>Miembros</h1>
  <button *ngIf="role==='ADMIN'" routerLink="/members/create">Nuevo</button>
  <ul><li *ngFor="let m of members">{{m.firstName}} {{m.lastName}}</li></ul>
`})
export class MembersListComponent implements OnInit {
  members: GymMemberResponseDTO[] = [];
  role = this.auth.getRole();
  constructor(private api: GymMemberService, private auth: AuthService) {}
  ngOnInit() { this.api.getAll().subscribe(m => this.members = m); }
}
```

Formulario (ADMIN):
```ts
@Component({ selector: 'app-member-form', template: `
  <!-- formulario reactivo -->
`})
export class MemberFormComponent { /* create/update usando GymMemberService */ }
```

### 10) Manejo de errores de negocio

Puedes crear un interceptor de errores global:
```ts
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        // mostrar toast/snackbar con err.error.message si existe
        return throwError(() => err);
      })
    );
  }
}
```

### 11) Testing básico

- Unit tests para `AuthInterceptor` y `AuthGuard` usando TestBed y HttpClientTestingModule.
- Prueba de rutas con `RouterTestingModule` verificando redirecciones a `/login` / `/forbidden` según rol.

### 12) Consejos de seguridad

- Usa HTTPS en producción.
- Considera usar Refresh Tokens en backend (no incluido por defecto aquí). Si se implementa, añade un `RefreshService` en Angular.
- Minimiza datos sensibles en LocalStorage; alternativa: usar cookies httpOnly si el backend lo soporta (cambia el flujo de envío de token).
- Valida formularios del lado del cliente y servidor.

Con esto podrás consumir los endpoints mostrados (por ejemplo `/api/v1/gym-members`) respetando la seguridad y roles definidos en el backend.
