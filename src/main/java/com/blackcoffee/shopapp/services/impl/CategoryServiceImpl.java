package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.dto.CategoryDto;
import com.blackcoffee.shopapp.model.Category;
import com.blackcoffee.shopapp.repository.CategoryRepository;
import com.blackcoffee.shopapp.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;
    @Override
    public Category createCategory(CategoryDto categoryDto) {
        Category category= new Category().builder()
                .name(categoryDto.getName())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(()->  new RuntimeException("Category not found"));

    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(long id, CategoryDto categoryDto) {
        Category existCategory= categoryRepository.findById(id).orElseThrow(()->  new RuntimeException("Category not found"));
        existCategory.setName(categoryDto.getName());
        return categoryRepository.save(existCategory);
    }

    @Override
    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }
}
