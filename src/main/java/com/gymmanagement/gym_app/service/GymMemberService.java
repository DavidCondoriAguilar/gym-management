package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.model.GymMemberModel;

import java.util.UUID;

public interface GymMemberService {
    GymMemberModel findById(UUID id);
}
