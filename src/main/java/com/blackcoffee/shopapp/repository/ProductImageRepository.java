package com.blackcoffee.shopapp.repository;

import com.blackcoffee.shopapp.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductId(long productId);
    ProductImage findByName(String name);

    @Query(nativeQuery = true, value = "select * from product_images p where p.product_id=:productId")
    List<ProductImage> getListImageByProductId(@Param("productId")Long productId);
}
