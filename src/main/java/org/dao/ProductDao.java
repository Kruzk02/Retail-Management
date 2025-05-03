package org.dao;

import org.model.Product;

public interface ProductDao {
    Boolean isProductExists(Long id);
    Product save(Product product);
    Product findById(Long id);
    Product update(Long id, Product product);
    int deleteById(Long id);
}
