package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.Usuario;
import com.gymmanagement.gym_app.domain.enums.Role;
import com.gymmanagement.gym_app.dto.request.LoginRequestDTO;
import com.gymmanagement.gym_app.dto.request.RegisterRequestDTO;
import com.gymmanagement.gym_app.dto.response.AuthResponseDTO;
import com.gymmanagement.gym_app.exception.AuthenticationException;
import com.gymmanagement.gym_app.exception.UserAlreadyExistsException;
import com.gymmanagement.gym_app.exception.UserNotFoundException;
import com.gymmanagement.gym_app.repository.UserRepository;
import com.gymmanagement.gym_app.security.JwtService;
import com.gymmanagement.gym_app.service.AuthService;
import com.gymmanagement.gym_app.validation.AuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthValidator authValidator;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Iniciando proceso de registro para usuario: {}", request.getUsername());

        try {
            authValidator.validateRegisterRequest(request);
            validateUserDoesNotExist(request.getUsername(), request.getEmail());

            Usuario user = buildUserFromRequest(request);
            Usuario savedUser = userRepository.save(user);

            String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole());
            logUserRegistration(savedUser);

            return new AuthResponseDTO(token, savedUser.getRole().name());

        } catch (Exception e) {
            log.error("Error durante el registro del usuario: {}", request.getUsername(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Iniciando proceso de login para email: {}", request.getEmail());

        try {
            authValidator.validateLoginRequest(request);
            Usuario user = findUserByEmail(request.getEmail().toLowerCase());

            validateLoginAttempts(user);
            validateUserCredentials(user, request.getPassword());
            validateUserStatus(user);

            resetFailedLoginAttempts(user);
            updateLastLogin(user);

            String token = jwtService.generateToken(user.getUsername(), user.getRole());
            log.info("Login exitoso para usuario: {}", user.getUsername());

            return new AuthResponseDTO(token, user.getRole().name());

        } catch (Exception e) {
            log.error("Error durante el login para email: {}", request.getEmail(), e);
            handleFailedLoginAttempt(request.getEmail());
            throw e;
        }
    }

    private void validateUserDoesNotExist(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(email.toLowerCase())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }
    }

    private Usuario buildUserFromRequest(RegisterRequestDTO request) {
        return Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(determineUserRole(request.getRole()))
                .enabled(true)
                .failedLoginAttempts(0)
                .accountNonLocked(true)
                .build();
    }

     /** Determines the role of a user based on the given requested role.
     * If the requested role is null, the default role is USER.
     *
     * @param requestedRole the role requested by the user
     * @return the role determined by the request
     */
    private Role determineUserRole(Role requestedRole) {
        return requestedRole != null ? requestedRole : Role.USER;
    }

    private Usuario findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    private void validateUserCredentials(Usuario user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthenticationException("Credenciales inválidas");
        }
    }

    private void validateUserStatus(Usuario user) {
        if (!user.isEnabled()) {
            throw new AuthenticationException("La cuenta está deshabilitada");
        }

        if (!user.isAccountNonLocked()) {
            throw new AuthenticationException("La cuenta está bloqueada temporalmente debido a múltiples intentos fallidos");
        }
    }

    /**
     * Validate the number of failed login attempts against the threshold.
     *
     * <p>If the number of failed login attempts is greater than or equal to the threshold, the user's account is
     * locked for a specified duration. If the lock duration has passed, the failed login attempts are reset to 0.
     *
     * @param user the user to validate
     * @throws AuthenticationException if the account is locked
     */
    private void validateLoginAttempts(Usuario user) {
        if (user.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            if (user.getLastFailedLogin() != null &&
                    user.getLastFailedLogin().plusMinutes(ACCOUNT_LOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                throw new AuthenticationException("Demasiados intentos fallidos. Por favor, intente más tarde.");
            } else {
                // Reset failed attempts if lock duration has passed
                user.setFailedLoginAttempts(0);
                user.setAccountNonLocked(true);
                userRepository.save(user);
            }
        }
    }

    private void handleFailedLoginAttempt(String email) {
        try {
            userRepository.findByEmail(email).ifPresent(user -> {
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);
                user.setLastFailedLogin(LocalDateTime.now());

                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                    user.setAccountNonLocked(false);
                    log.warn("Cuenta bloqueada para el usuario: {}", user.getUsername());
                }

                userRepository.save(user);
            });
        } catch (Exception e) {
            log.error("Error al actualizar los intentos de inicio de sesión fallidos", e);
        }
    }

    private void resetFailedLoginAttempts(Usuario user) {
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setAccountNonLocked(true);
            userRepository.save(user);
        }
    }

    private void updateLastLogin(Usuario user) {
        try {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            log.warn("No se pudo actualizar el último login para el usuario: {}", user.getUsername(), e);
        }
    }

    private void logUserRegistration(Usuario user) {
        log.info("""
            ====================================
            NUEVO USUARIO REGISTRADO
            Usuario: {}
            Email: {}
            Rol: {}
            Fecha: {}
            ====================================
            """,
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                LocalDateTime.now()
        );
    }
}