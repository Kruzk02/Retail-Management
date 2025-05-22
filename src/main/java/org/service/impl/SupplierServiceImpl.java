package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.SupplierDao;
import org.dto.SupplierRequest;
import org.exception.DataNotFoundException;
import org.exception.InvalidValidatorException;
import org.model.Supplier;
import org.service.SupplierService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.validators.SupplierRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierDao supplierDao;
    private final SupplierRequestValidator supplierRequestValidator;

    private void validationDTO(SupplierRequest request) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "supplierRequest");
        supplierRequestValidator.validate(request, errors);

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new InvalidValidatorException(errorMessages);
        }
    }

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
        validationDTO(request);

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
        validationDTO(request);

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
