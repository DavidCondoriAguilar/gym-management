package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.domain.enums.Role;
import com.gymmanagement.gym_app.dto.response.UserResponseDTO;
import com.gymmanagement.gym_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios", 
              description = "Obtiene una lista de todos los usuarios registrados. Requiere rol ADMIN")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuarios por rol", 
              description = "Obtiene una lista de usuarios filtrados por rol. Requiere rol ADMIN")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
}
