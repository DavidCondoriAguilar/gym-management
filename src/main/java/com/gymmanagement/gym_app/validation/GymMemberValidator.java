package com.gymmanagement.gym_app.validation;

import com.gymmanagement.gym_app.model.GymMemberModel;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class GymMemberValidator implements Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{9}$");

    @Override
    public boolean supports(Class<?> clazz) {
        return GymMemberModel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GymMemberModel gymMember = (GymMemberModel) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "Name is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required", "Email is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "field.required", "Phone number is required.");

        if (!EMAIL_PATTERN.matcher(gymMember.getEmail()).matches()) {
            errors.rejectValue("email", "field.invalid", "Invalid email format.");
        }

        if (gymMember.getPhone() != null && !PHONE_PATTERN.matcher(gymMember.getPhone()).matches()) {
            errors.rejectValue("phone", "field.invalid", "Phone number must be exactly 9 digits.");
        }

        if (gymMember.getMembershipStart() == null || gymMember.getMembershipEnd() == null) {
            errors.rejectValue("membershipStart", "field.required", "Membership start date is required.");
            errors.rejectValue("membershipEnd", "field.required", "Membership end date is required.");
        } else if (gymMember.getMembershipEnd().isBefore(gymMember.getMembershipStart())) {
            errors.rejectValue("membershipEnd", "field.invalid", "Membership end date must be after start date.");
        }
    }
}
