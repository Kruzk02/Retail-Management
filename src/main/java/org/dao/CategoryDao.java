package org.dao;

import org.model.Category;

import java.util.List;

public interface CategoryDao {
    Boolean isCategoryExists(Long id, String name);
    Category save(Category category);
    List<Category> findCategoryByProductId(Long productId);
    Category findByName(String name);
    int deleteById(Long id);
}
