package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.dto.CategoryDto;
import com.blackcoffee.shopapp.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto category);
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long id, CategoryDto category);
    void deleteCategory(long id);
}
