package org.validators;

import org.dto.ProductRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

public class ProductRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ProductRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "Product name must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "description", "description.empty", "Product description must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "categories", "categories.empty", "Categories must not be empty");
        ValidationUtils.rejectIfEmpty(errors, "price", "price.empty", "Price must not be empty");

        ProductRequest request = (ProductRequest) target;

        if (request.price().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("price", "price.zero", "Price must not be negative.");
        }

        for (var category : request.categories()) {
            if (category == null || category.isBlank()) {
                errors.rejectValue("category", "category.empty", "Category list must not contain null or blank values.");
            }
        }
    }
}
