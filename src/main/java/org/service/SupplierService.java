package org.service;

import org.dto.SupplierRequest;
import org.model.Supplier;

import java.util.List;

public interface SupplierService {
    List<Supplier> findAll(int limit, int offset);
    Supplier findById(Long id);
    Supplier save(SupplierRequest request);
    Supplier update(Long id, SupplierRequest request);
    int deleteById(Long id);
}
