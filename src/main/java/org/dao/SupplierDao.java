package org.dao;

import org.model.Supplier;

import java.util.List;

public interface SupplierDao {
    Boolean isSupplierExists(Long id);
    Supplier save(Supplier supplier);
    List<Supplier> findAll(int limit, int offset);
    Supplier findById(Long id);
    Supplier update(Long id, Supplier supplier);
    int deleteById(Long id);
}
