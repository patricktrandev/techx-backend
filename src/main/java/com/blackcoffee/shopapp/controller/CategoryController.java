package com.blackcoffee.shopapp.controller;

import com.blackcoffee.shopapp.dto.CategoryDto;
import com.blackcoffee.shopapp.model.Category;
import com.blackcoffee.shopapp.response.CategoryResponse;
import com.blackcoffee.shopapp.services.CategoryService;
import com.blackcoffee.shopapp.utils.LocalizationUtils;
import com.blackcoffee.shopapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    private final WebUtils webUtils;

    @GetMapping()
    public ResponseEntity<List<Category>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    @PostMapping()
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errors);
        }
        Category createdCategory= categoryService.createCategory(categoryDto);
        return new ResponseEntity<>("Category created successfully",HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") int id,@Valid @RequestBody CategoryDto categoryDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<String> errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errors);
        }
        categoryService.updateCategory(id, categoryDto);

        return ResponseEntity.ok(CategoryResponse.builder()
                .message(localizationUtils.getLocalizedMessage("category.update_category.update_successfully",webUtils ))
                .build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") int id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}
