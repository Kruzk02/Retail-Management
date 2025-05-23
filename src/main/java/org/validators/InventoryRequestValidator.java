package org.validators;

import org.dto.InventoryRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class InventoryRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return InventoryRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "productId", "productId.empty", "Product id cannot less than or equal 0");
        ValidationUtils.rejectIfEmpty(errors, "locationId", "location.empty", "Location id cannot less than or equal 0");
    }
}
