package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.Usuario;
import com.gymmanagement.gym_app.domain.enums.Role;
import com.gymmanagement.gym_app.dto.request.LoginRequestDTO;
import com.gymmanagement.gym_app.dto.request.RegisterRequestDTO;
import com.gymmanagement.gym_app.dto.response.AuthResponseDTO;
import com.gymmanagement.gym_app.repository.UserRepository;
import com.gymmanagement.gym_app.security.JwtService;
import com.gymmanagement.gym_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtil;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El usuario o email ya existe");
        }
        // Solo permitir ADMIN si ya hay al menos un admin en la base de datos
        Role roleToAssign = Role.USER;
        if (request.getRole() == Role.ADMIN) {
            boolean adminExists = userRepository.existsByRole(Role.ADMIN);
            if (adminExists) {
                // Solo un admin ya registrado puede crear otro admin (esto es solo ejemplo, puedes personalizar)
                throw new RuntimeException("No tienes permisos para crear un usuario ADMIN desde el registro público");
            } else {
                // Si no hay ningún admin, permitimos crear el primero
                roleToAssign = Role.ADMIN;
            }
        }
        Usuario user = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleToAssign)
                .enabled(true)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponseDTO(token, user.getRole().name());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Buscar por email en lugar de username
        Usuario user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!user.isEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        // Seguimos usando el username en el token para mantener consistencia
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponseDTO(token, user.getRole().name());
    }
}
