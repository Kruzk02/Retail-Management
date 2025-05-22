package org.validators;

import org.dto.SupplierRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SupplierRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SupplierRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "Supplier name must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "contact", "contact.empty", "Contact must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "phoneNumber", "phoneNumber.empty", "Phone number must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "email", "email.empty", "Email must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "address", "address.empty", "Address must not be empty");

        SupplierRequest request = (SupplierRequest) target;

        if (request.name().length() < 2) {
            errors.rejectValue("name", "name.length", "Name cannot be less than 2 character");
        }

        if (!request.phoneNumber().matches("^[+]?[(]?[-1-9]{1,4}[)]?[-\\s./0-9]*$")) {
            errors.rejectValue("phoneNumber", "phoneNumber.invalid", "Enter a valid phone number using only digits, spaces, dashes, parentheses, or an optional '+' at the start. Maximum 16 characters");
        }

        if (!request.email().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.rejectValue("email", "email.invalid", "Invalid email format");
        }
    }
}
