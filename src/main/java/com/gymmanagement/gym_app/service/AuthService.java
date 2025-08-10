package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.LoginRequestDTO;
import com.gymmanagement.gym_app.dto.request.RegisterRequestDTO;
import com.gymmanagement.gym_app.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}
