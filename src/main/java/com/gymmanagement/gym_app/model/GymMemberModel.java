package com.gymmanagement.gym_app.model;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GymMemberModel {

    private UUID id;
    private String nombre;
    private String email;
    private String telefono;
    private Boolean activo;
    private LocalDate fechaRegistro;
    private LocalDate membershipStart;
    private LocalDate membershipEnd;

    private MembershipPlanModel membershipPlan;
    private List<PaymentModel> pagos;
    private List<MembershipRecordModel> membershipRecords;
    private List<PromotionModel> promociones;
}
