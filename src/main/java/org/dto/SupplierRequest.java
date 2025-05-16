package org.dto;

public record SupplierRequest(
    String name,
    String contactName,
    String phoneNumber,
    String email,
    String address
) {

    public SupplierRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Name cannot be less than 2 character");
        }

        if (contactName == null || contactName.isBlank()) {
            throw new IllegalArgumentException("Contact name cannot be blank");
        }

        if (phoneNumber == null || !phoneNumber.matches("^[+]?[(]?[-1-9]{1,4}[)]?[-\\s./0-9]*$")) {
            throw new IllegalArgumentException("Enter a valid phone number using only digits, spaces, dashes, parentheses, or an optional '+' at the start. Maximum 16 characters");
        }

        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be blank");
        }
    }

}
