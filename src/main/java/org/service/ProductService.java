package org.service;

import org.dto.ProductRequest;
import org.model.Product;

public interface ProductService {
    Product save(ProductRequest request);
    Product findById(Long id);
    Product update(Long id, ProductRequest request);
    int deleteById(Long id);
}
