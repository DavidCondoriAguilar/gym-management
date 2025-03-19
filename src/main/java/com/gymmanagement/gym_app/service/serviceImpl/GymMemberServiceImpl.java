package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.model.GymMemberModel;
import com.gymmanagement.gym_app.service.GymMemberService;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GymMemberServiceImpl implements GymMemberService {
    @Override
    public GymMemberModel findById(UUID id) {
        // Implementación aquí
        return null;
    }
}
