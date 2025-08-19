package com.gymmanagement.gym_app.validation;
import com.gymmanagement.gym_app.dto.request.LoginRequestDTO;
import com.gymmanagement.gym_app.dto.request.RegisterRequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;
@Component
public class AuthValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{3,20}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    public void validateRegisterRequest(RegisterRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de registro no puede ser nula");
        }

        validateUsername(request.getUsername());
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
    }

    public void validateLoginRequest(LoginRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de inicio de sesión no puede ser nula");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("El email es requerido");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
    }

    private void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("El nombre de usuario es requerido");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("""
                El nombre de usuario debe:
                - Tener entre 3 y 20 caracteres
                - Contener solo letras, números, puntos, guiones bajos y guiones
                - No contener espacios en blanco
                """);
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("El email es requerido");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula");
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial (@#$%^&+=)");
        }
        if (password.contains(" ")) {
            throw new IllegalArgumentException("La contraseña no debe contener espacios en blanco");
        }
    }
}