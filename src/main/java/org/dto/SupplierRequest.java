package org.dto;

public record SupplierRequest(
    String name,
    String contactName,
    String phoneNumber,
    String email,
    String address
) { }
