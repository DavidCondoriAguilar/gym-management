# Planes de Membresía y Sistema de Pagos

## Flujo de Pagos y Descuentos

### 1. Proceso de Pago

1. **Registro del Miembro**
   - Se crea un registro de membresía (`MembershipRecord`)
   - Se asocia el plan de membresía seleccionado
   - Se aplican promociones vigentes si existen

2. **Cálculo del Pago**
   - El sistema calcula el monto total
   - Aplica descuentos si hay promociones vigentes
   - Genera el monto final a pagar

### 2. Ejemplo de Respuesta de la API

```json
{
    "memberId": "56670e7d-6093-44d6-b747-6b45a7065343",
    "memberName": "Juan Pérez",
    "totalPaid": 0,
    "totalWithDiscount": 0.00,
    "remainingBalance": 0.00,
    "isFullyPaid": true,
    "activePromotionId": "45fe3f6a-a44f-4aa4-8c1a-40f27123bf62",
    "activePromotionName": "ACCESO FULL DAY",
    "discountPercentage": 30.00
}
```

### 3. Lógica de Cálculo de Descuentos

1. **Fórmula de Descuento**
   ```
   Monto del Descuento = Costo del Plan * (Porcentaje de Descuento / 100)
   Total con Descuento = Costo del Plan - Monto del Descuento
   ```

2. **Ejemplo Práctico**
   - **Plan**: Mensual ($150.00)
   - **Promoción**: 30% de descuento
   - **Cálculo**:
     - Descuento: $150 * 0.30 = $45
     - Total con descuento: $150 - $45 = $105

## Planes de Membresía Disponibles

## Available Membership Plans

### 1. Daily Access
- **Code**: `DIARIO`
- **Description**: Access to the gym for one day
- **Price**: $10.00
- **Features**: 
  - Access to all gym facilities
  - Locker usage
  - Basic amenities

### 2. Weekly Access
- **Code**: `SEMANAL`
- **Description**: 7 days of unlimited access
- **Price**: $50.00
- **Features**:
  - All Daily Access features
  - 10% discount on personal training sessions
  - 1 free group class per week

### 3. Monthly Access
- **Code**: `MENSUAL`
- **Description**: 30 days of unlimited access
- **Price**: $150.00
- **Features**:
  - All Weekly Access features
  - 15% discount on personal training
  - 2 free group classes per week
  - 1 free guest pass per month

### 4. Quarterly Access
- **Code**: `TRIMESTRAL`
- **Description**: 90 days of unlimited access
- **Price**: $400.00 (save $50 compared to monthly)
- **Features**:
  - All Monthly Access features
  - 20% discount on personal training
  - 3 free group classes per week
  - 2 free guest passes per month
  - Free fitness assessment

### 5. Annual Access
- **Code**: `ANUAL`
- **Description**: 365 days of unlimited access
- **Price**: $1,200.00 (save $600 compared to monthly)
- **Features**:
  - All Quarterly Access features
  - 25% discount on personal training
  - Unlimited group classes
  - 4 free guest passes per month
  - Free fitness assessment every 3 months
  - Free locker rental
  - 10% discount on merchandise

## Discounts and Promotions

### How Discounts Work
1. **Automatic Application**: Discounts are automatically applied at checkout when eligible
2. **Stacking**: Only one discount can be applied per transaction
3. **Membership Upgrades**: Prorated discounts may apply when upgrading plans

### Current Promotions

#### 1. Full Day Access (FULL DAY)
- **Code**: `FULL_DAY`
- **Discount**: 30% off
- **Applicable To**: All membership plans
- **Validity**: Check current promotions for expiration
- **Notes**: Cannot be combined with other offers

#### 2. Student Discount
- **Code**: `STUDENT`
- **Discount**: 15% off
- **Requirements**: Valid student ID required
- **Applicable To**: Monthly, Quarterly, and Annual plans only

#### 3. Family Plan
- **Code**: `FAMILY`
- **Discount**: 10% off for each additional family member
- **Requirements**: Minimum of 2 family members
- **Applicable To**: All membership plans

## Payment Status

Your payment status includes:
- **Total Paid**: Sum of all completed payments
- **Total with Discount**: Membership cost after applying active promotions
- **Remaining Balance**: Amount still to be paid (may be negative if overpaid)
- **Active Promotion**: Currently applied discount, if any

## Frequently Asked Questions

### How do I apply a promotion?
Promotions can be applied during the membership purchase or renewal process. Enter the promotion code in the designated field.

### Can I change my membership plan?
Yes, you can upgrade or downgrade your plan. Any remaining balance will be prorated.

### What happens if I don't use all my guest passes?
Guest passes expire at the end of each month and do not roll over.

### How do I check my payment status?
You can view your payment status through the member portal or by contacting member services.

## Contact
For questions about memberships or promotions, please contact:
- Email: memberships@gymapp.com
- Phone: (555) 123-4567
- In-person: Visit the front desk during business hours
