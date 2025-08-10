package com.gymmanagement.gym_app.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

@Data
public class GymMemberRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,9}$", message = "Teléfono inválido")
    private String phone;

    @NotNull
    private Boolean active;

    @NotNull
    private LocalDate registrationDate;

    @NotNull
    private LocalDate membershipStartDate;

    @NotNull
    private LocalDate membershipEndDate;

    @NotNull
    private UUID membershipPlanId;

    // Allow associating promotions with a member
    private List<UUID> promotionIds;
}
