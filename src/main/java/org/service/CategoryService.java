package org.service;

import org.model.Category;

public interface CategoryService {
    Category findByName(String name);
    Category save(String name);
    int deleteById(Long id);
}
