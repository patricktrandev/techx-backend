package com.blackcoffee.shopapp.services;

import com.blackcoffee.shopapp.dto.ProductDto;
import com.blackcoffee.shopapp.dto.ProductImageDto;
import com.blackcoffee.shopapp.exception.InvalidParamsException;
import com.blackcoffee.shopapp.model.Product;
import com.blackcoffee.shopapp.model.ProductImage;
import com.blackcoffee.shopapp.response.ProductImageResponse;
import com.blackcoffee.shopapp.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(ProductDto productDto);
    ProductImage getImageByName(String name);
    Product getProductById(long id);
    Page<ProductResponse> getAllProducts(PageRequest pageRequest, Long categoryId, String keyword);
    Product updateProduct(long id, ProductDto productDto);
    Product updateProductThumbnail(long id, Product product);
    void deleteProduct(long id);
    boolean existByName(String name);
    ProductImage createProductImage(Long productId, ProductImageDto productImageDto) throws InvalidParamsException;
    List<ProductImageResponse> getListProductImages(Long productId);
    List<ProductResponse> getListProductByIds(List<Long> ids);
    ProductImage saveImage(Long productId, byte[] imageData, String uniqueFileName) throws IOException;
}
