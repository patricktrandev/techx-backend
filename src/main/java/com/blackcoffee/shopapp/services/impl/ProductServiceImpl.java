package com.blackcoffee.shopapp.services.impl;

import com.blackcoffee.shopapp.dto.ProductDto;
import com.blackcoffee.shopapp.dto.ProductImageDto;
import com.blackcoffee.shopapp.exception.DataNotFoundException;
import com.blackcoffee.shopapp.exception.InvalidParamsException;
import com.blackcoffee.shopapp.model.Category;
import com.blackcoffee.shopapp.model.Product;
import com.blackcoffee.shopapp.model.ProductImage;
import com.blackcoffee.shopapp.repository.CategoryRepository;
import com.blackcoffee.shopapp.repository.ProductImageRepository;
import com.blackcoffee.shopapp.repository.ProductRepository;
import com.blackcoffee.shopapp.response.ProductImageResponse;
import com.blackcoffee.shopapp.response.ProductResponse;
import com.blackcoffee.shopapp.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    public Product createProduct(ProductDto productDto) {
        Category existCategory =categoryRepository.findById(productDto.getCategoryId()).orElseThrow(()-> new DataNotFoundException("Category is invalid"));

        Product savedProduct=Product.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .thumbnail(productDto.getThumbnail())
                .description(productDto.getDescription())
                .category(existCategory)
                .isActive(1)
                .build();

        return productRepository.save(savedProduct);
    }

    @Override
    public ProductImage getImageByName(String name) {
        ProductImage productImage=productImageRepository.findByName(name);
        if(productImage==null){
            throw new DataNotFoundException("Product does not exists with name "+name);
        }
        return productImage;
    }

    @Override
    public Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Product does not exists with id "+id));

    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest,  Long categoryId, String keyword) {

        return productRepository.searchProducts(categoryId,keyword,pageRequest).map(product -> {
            ProductResponse productResponse=ProductResponse.builder()
                    .name(product.getName())
                    .id(product.getId())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .isActive(product.getIsActive())
                    .description(product.getDescription())
                    .categoryId(product.getCategory().getId())

                    .build();
            productResponse.setCreatedAt(product.getCreatedAt());
            productResponse.setUpdatedAt(product.getUpdatedAt());
            return productResponse;
        });
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDto productDto) {
        Product foundProduct=productRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Product does not exists with id= "+id));
        Category existCategory =categoryRepository.findById(productDto.getCategoryId()).orElseThrow(()-> new DataNotFoundException("Category is invalid"));
        if(foundProduct!= null){
            foundProduct.setName(productDto.getName());
            foundProduct.setPrice(productDto.getPrice());

            foundProduct.setDescription(productDto.getDescription());
            foundProduct.setCategory(existCategory);
            return productRepository.save(foundProduct);
        }
        return null;

    }

    @Override
    public Product updateProductThumbnail(long id, Product product) {
        Product foundProduct=productRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Product does not exists with id= "+id));
        Category existCategory =categoryRepository.findById(product.getCategory().getId()).orElseThrow(()-> new DataNotFoundException("Category is invalid"));
        if(foundProduct!= null){
            foundProduct.setThumbnail(product.getThumbnail());
            return productRepository.save(foundProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        Product foundProduct=productRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Product does not exists with id "+id));
        if(foundProduct!=null){
            foundProduct.setIsActive(0);
            productRepository.save(foundProduct);
        }


    }

    @Override
    public boolean existByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDto productImageDto) throws InvalidParamsException {
        Product foundProduct=productRepository.findById(productImageDto.getProductId()).orElseThrow(()-> new DataNotFoundException("Product does not exists with id= "+productImageDto.getProductId()));
        ProductImage newProductImage=ProductImage.builder()
                .product(foundProduct)
                .imageUrl(productImageDto.getImageUrl())
                .build();
        //no more than 5 images at once
        int size=productImageRepository.findByProductId(productId).size();
        if(size>=5){
            throw new InvalidParamsException("Image upload exceeded 5.");
        }
        return productImageRepository.save(newProductImage);

    }

    @Override
    public List<ProductImageResponse> getListProductImages(Long productId) {
        List<ProductImage> productImageList=productImageRepository.getListImageByProductId(productId);
        List<ProductImageResponse> responses= new ArrayList<>();
        for(ProductImage p:productImageList){

            responses.add(ProductImageResponse.builder()
                    .id(p.getId())
                    .productId(p.getProduct().getId())
                    .imageUrl(p.getName())
                    .build());
        }
        return responses;
    }

    @Override
    public List<ProductResponse> getListProductByIds(List<Long> ids) {
        List<Product> productList=productRepository.getProductsByIds(ids);
        List<ProductResponse> productResponseList= new ArrayList<>();
        for(Product p: productList){
            productResponseList.add(ProductResponse.builder()
                            .name(p.getName())
                            .id(p.getId())
                            .description(p.getDescription())
                            .categoryId(p.getCategory().getId())
                            .isActive(p.getIsActive())
                            .thumbnail(p.getThumbnail())
                            .price(p.getPrice())
                            .build());
        }
        return productResponseList;
    }

    @Override
    public ProductImage saveImage(Long productId, byte[] imageData, String uniqueFileName) throws IOException {
        Product foundProduct=productRepository.findById(productId).orElseThrow(()-> new DataNotFoundException("Product does not exists with id= "+productId));

        return productImageRepository.save(ProductImage.builder()
                        .product(foundProduct)
                        .name(uniqueFileName)
                        .imageUrl(imageData)
                .build());
    }
}
