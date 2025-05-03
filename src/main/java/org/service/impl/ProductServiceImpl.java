package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.CategoryDao;
import org.dao.ProductDao;
import org.dto.ProductRequest;
import org.exception.DataNotFoundException;
import org.model.Category;
import org.model.Product;
import org.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final CategoryDao categoryDao;

    @Override
    public Product save(ProductRequest request) {
        Collection<Category> categories = request.categories().stream()
                .map(categoryDao::findByName)
                .toList();
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .quantity(request.quantity())
                .categories(categories)
                .build();
        return productDao.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productDao.findById(id);
    }

    @Override
    public Product update(Long id, ProductRequest request) {
        Boolean result = productDao.isProductExists(id);
        if (!result) {
            throw new DataNotFoundException("Product not found with id: " + id);
        }

        List<Category> categories = categoryDao.findCategoryByProductId(id);

        Product product = productDao.findById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setCategories(categories);

        return productDao.update(id, product);
    }

    @Override
    public int deleteById(Long id) {
        Boolean result = productDao.isProductExists(id);
        if (!result) {
            throw new DataNotFoundException("Product not found with id: " + id);
        }
        return productDao.deleteById(id);
    }
}
