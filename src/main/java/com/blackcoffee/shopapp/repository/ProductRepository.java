package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>{
   boolean existsByName(String name);
   Optional<Product> findById(Long id);

   @Query("SELECT p FROM Product p where " +
           "(:categoryId IS NULL OR :categoryId =0 OR p.category.id= :categoryId) " +
           "AND (:keyword IS NULL OR :keyword= '' or p.name like %:keyword% or p.description like %:keyword%)")
   Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                @Param("keyword")String keyword, Pageable pageable);
   @Query("SELECT p from Product p where p.id in :productIds")
   List<Product> getProductsByIds(List<Long> productIds);
}
