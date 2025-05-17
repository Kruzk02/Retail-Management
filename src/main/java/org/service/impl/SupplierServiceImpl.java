package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.SupplierDao;
import org.dto.SupplierRequest;
import org.exception.DataNotFoundException;
import org.model.Supplier;
import org.service.SupplierService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierDao supplierDao;

    @Override
    public List<Supplier> findAll(int limit, int offset) {
        return supplierDao.findAll(limit, offset);
    }

    @Override
    public Supplier findById(Long id) {
        return supplierDao.findById(id);
    }

    @Override
    public Supplier save(SupplierRequest request) {
        return supplierDao.save(Supplier.builder()
                .name(request.name())
                .contactName(request.contactName())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .address(request.address())
        .build());
    }

    @Override
    public Supplier update(Long id, SupplierRequest request) {
        Boolean result = supplierDao.isSupplierExists(id);
        if (!result) {
            throw new DataNotFoundException("Supplier not found with a id: " + id);
        }

        Supplier supplier = supplierDao.findById(id);
        supplier.setName(request.name());
        supplier.setContactName(request.contactName());
        supplier.setPhoneNumber(request.phoneNumber());
        supplier.setEmail(request.email());
        supplier.setAddress(request.address());
        supplier.setUpdatedAt(LocalDateTime.now());

        return supplierDao.update(id, supplier);
    }

    @Override
    public int deleteById(Long id) {
       Boolean result = supplierDao.isSupplierExists(id);
       if (!result) {
           throw new DataNotFoundException("Supplier not found with a id: " + id);
       }

       return supplierDao.deleteById(id);
    }
}
