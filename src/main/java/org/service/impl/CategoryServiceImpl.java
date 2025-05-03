package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.CategoryDao;
import org.exception.DataNotFoundException;
import org.model.Category;
import org.service.CategoryService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    @Override
    public Category findByName(String name) {
        return categoryDao.findByName(name);
    }

    @Override
    public Category save(String name) {
        Boolean isExists = categoryDao.isCategoryExists(null ,name);
        if (!isExists) {
            throw new DataNotFoundException("Category not found with a name: " + name);
        }

        return categoryDao.save(Category.builder().name(name).build());
    }

    @Override
    public int deleteById(Long id) {
        Boolean isExists = categoryDao.isCategoryExists(id, null);
        if (!isExists) {
            throw new DataNotFoundException("Category not found with a id: " + id);
        }

        return categoryDao.deleteById(id);
    }
}
