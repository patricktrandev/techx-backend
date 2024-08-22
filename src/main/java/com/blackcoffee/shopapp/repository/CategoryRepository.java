package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
