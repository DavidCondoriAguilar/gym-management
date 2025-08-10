package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.dto.request.LoginRequestDTO;
import com.gymmanagement.gym_app.dto.request.RegisterRequestDTO;
import com.gymmanagement.gym_app.dto.response.AuthResponseDTO;
import com.gymmanagement.gym_app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para login y registro de usuarios")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Registro de usuario", description = "Registra un nuevo usuario y retorna un JWT.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login de usuario", description = "Autentica un usuario y retorna un JWT.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
