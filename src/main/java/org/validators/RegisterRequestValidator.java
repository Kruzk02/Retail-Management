package org.validators;

import org.dto.RegisterRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class RegisterRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "username", "username.empty", "Username should not be empty");
        ValidationUtils.rejectIfEmpty(errors, "email", "email.empty", "Email should not be empty");
        ValidationUtils.rejectIfEmpty(errors, "password", "password.empty", "Password should not be empty");

        RegisterRequest request = (RegisterRequest) target;
        if (request.username() != null && request.username().length() < 8) {
            errors.rejectValue("username", "username.length",  "Username cannot be less than 8 characters");
        }

        if (!request.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.rejectValue("email", "email.invalid", "Invalid email format");
        }

        if (request.password() != null && request.password().length() < 8) {
            errors.rejectValue("password", "password.length", "password cannot be less than 8 characters");
        }
    }
}
