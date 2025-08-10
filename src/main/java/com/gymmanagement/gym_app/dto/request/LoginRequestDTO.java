package com.gymmanagement.gym_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;  // Cambiado de 'username' a 'email'

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
