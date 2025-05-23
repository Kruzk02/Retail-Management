package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.CategoryDao;
import org.dao.ProductDao;
import org.dto.ProductRequest;
import org.exception.DataNotFoundException;
import org.exception.InvalidValidatorException;
import org.model.Category;
import org.model.Product;
import org.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.validators.ProductRequestValidator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final ProductRequestValidator productRequestValidator;

    private void validationDTO(ProductRequest request) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "productRequest");
        productRequestValidator.validate(request, errors);

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new InvalidValidatorException(errorMessages);
        }
    }

    @Override
    public Product save(ProductRequest request) {
        validationDTO(request);

        Collection<Category> categories = request.categories().stream()
                .map(categoryDao::findByName)
                .toList();
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
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
        validationDTO(request);

        Boolean result = productDao.isProductExists(id);
        if (!result) {
            throw new DataNotFoundException("Product not found with id: " + id);
        }

        List<Category> categories = categoryDao.findCategoryByProductId(id);

        Product product = productDao.findById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
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
