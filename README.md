# Documentación de la API de Miembros del Gimnasio

## Descripción
Esta API permite gestionar la información de los miembros del gimnasio, incluyendo detalles personales, planes de membresía, pagos y registros de membresía.

---

## Endpoints Disponibles

### 1. Obtener la lista de miembros
**GET** `/members`

#### Respuesta esperada
```json
[
    {
        "id": "UUID",
        "name": "String",
        "email": "String",
        "phone": "String",
        "active": "Boolean",
        "registrationDate": "YYYY-MM-DD",
        "membershipStart": "YYYY-MM-DD",
        "membershipEnd": "YYYY-MM-DD",
        "membershipPlan": {
            "id": "UUID",
            "name": "String",
            "durationMonths": "Integer",
            "cost": "Decimal",
            "description": "String",
            "type": "String"
        },
        "payments": [
            {
                "id": "UUID",
                "gymMemberId": "UUID",
                "amount": "Decimal",
                "paymentDate": "YYYY-MM-DD",
                "paymentMethod": "String",
                "status": "String"
            }
        ],
        "membershipRecords": [
            {
                "id": "UUID",
                "gymMemberId": "UUID",
                "membershipPlanId": "UUID",
                "startDate": "YYYY-MM-DD",
                "endDate": "YYYY-MM-DD",
                "status": "String",
                "cancellationDate": "YYYY-MM-DD | null"
            }
        ],
        "promotions": []
    }
]
```

---

### 2. Obtener un miembro por ID
**GET** `/members/{id}`

#### Respuesta esperada
```json
{
    "id": "UUID",
    "name": "String",
    "email": "String",
    "phone": "String",
    "active": "Boolean",
    "registrationDate": "YYYY-MM-DD",
    "membershipStart": "YYYY-MM-DD",
    "membershipEnd": "YYYY-MM-DD",
    "membershipPlan": {
        "id": "UUID",
        "name": "String",
        "durationMonths": "Integer",
        "cost": "Decimal",
        "description": "String",
        "type": "String"
    },
    "payments": [...],
    "membershipRecords": [...],
    "promotions": []
}
```

---

### 3. Crear un nuevo miembro
**POST** `/members`

#### Cuerpo de la solicitud
```json
{
    "name": "String",
    "email": "String",
    "phone": "String",
    "membershipPlanId": "UUID"
}
```

#### Respuesta esperada
```json
{
    "id": "UUID",
    "name": "String",
    "email": "String",
    "phone": "String",
    "active": true,
    "registrationDate": "YYYY-MM-DD",
    "membershipStart": "YYYY-MM-DD",
    "membershipEnd": "YYYY-MM-DD",
    "membershipPlan": {
        "id": "UUID",
        "name": "String",
        "durationMonths": "Integer",
        "cost": "Decimal",
        "description": "String",
        "type": "String"
    }
}
```

---

### 4. Actualizar un miembro
**PUT** `/members/{id}`

#### Cuerpo de la solicitud
```json
{
    "name": "String",
    "email": "String",
    "phone": "String",
    "active": "Boolean"
}
```

#### Respuesta esperada
```json
{
    "message": "Miembro actualizado exitosamente"
}
```

---

### 5. Eliminar un miembro
**DELETE** `/members/{id}`

#### Respuesta esperada
```json
{
    "message": "Miembro eliminado exitosamente"
}
```

![pdfGym](https://github.com/user-attachments/assets/93900cae-e944-4214-88bb-5fc352af53e4)

![report](https://github.com/user-attachments/assets/939ee3c8-08a7-40ed-b2d7-215c756d56ab)

---

## Notas
- Todos los UUIDs son generados automáticamente por la base de datos.
- Los pagos y registros de membresía están asociados a un miembro y su plan de membresía.
- Las fechas siguen el formato `YYYY-MM-DD`.
- Se recomienda validar los datos antes de enviarlos a la API.

---
