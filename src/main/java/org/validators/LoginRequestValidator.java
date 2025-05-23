package org.validators;

import org.dto.LoginRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "username", "username.empty", "Username should not be empty");
        ValidationUtils.rejectIfEmpty(errors, "password", "password.empty", "Password should not be empty");
    }
}
