package com.blackcoffee.shopapp.controller;


import com.blackcoffee.shopapp.dto.ProductDto;
import com.blackcoffee.shopapp.dto.ProductImageDto;
import com.blackcoffee.shopapp.model.Product;
import com.blackcoffee.shopapp.model.ProductImage;
import com.blackcoffee.shopapp.response.ProductListResponse;
import com.blackcoffee.shopapp.response.ProductResponse;
import com.blackcoffee.shopapp.services.BaseRedisService;
import com.blackcoffee.shopapp.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sun.tools.jconsole.JConsoleContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@Tag(name = "CRUD REST API for Product Resource")
@RequiredArgsConstructor
public class ProductController {
    //private static  final Logger logger= LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final BaseRedisService baseRedisService;


    @Operation(
            summary = "Get all products by all users"
    )
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(@RequestParam(required = false) int page, @RequestParam(required = false) int limit,
                                                              @RequestParam(defaultValue = "0", name = "category_id")Long categoryId,
                                                              @RequestParam(defaultValue = "")String keyword){

        PageRequest pageable= PageRequest.of(page,limit, Sort.by("id").ascending());
        Page<ProductResponse> products= productService.getAllProducts(pageable,categoryId,keyword);
        int totalPages=products.getTotalPages();
        long totalElements=products.getTotalElements();
        List<ProductResponse> productList=products.getContent();

        return ResponseEntity.ok(ProductListResponse.builder()
                .products(productList)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build());
    }
    @Operation(
            summary = "Get product information by all users"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductDetailsById(@PathVariable("id") int id){
        return ResponseEntity.ok(productService.getProductById(id));
    }
    @Operation(
            summary = "Get list of products by admin"
    )
    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductListByIds(@RequestParam("ids") String ids){
        try{
            List<Long> productIds= Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toUnmodifiableList());
            return ResponseEntity.ok(productService.getListProductByIds(productIds));


        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}/images-list")
    @Operation(
            summary = "Get list of images by all users"
    )
    public ResponseEntity<?> getImageProductsByProductId(@PathVariable() Long id){
        try{
            return ResponseEntity.ok(productService.getListProductImages(id));


        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(
            summary = "Upload image and save image by blob data"
    )
    @PostMapping(value = "/uploads/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@PathVariable("id") long id , @ModelAttribute("files") List<MultipartFile> files,  @RequestHeader("Authorization") String token){
        try{
           Product foundProduct=productService.getProductById(id);
           int index=1;
            files = files==null? new ArrayList<MultipartFile>(): files;
            if(files.size()>5){
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images.");
            }
            List<ProductImageDto> imageList= new ArrayList<>();
            for(MultipartFile file: files){
                if(file.getSize()==0){
                    continue;
                }
                if(file.getSize()> 10*1024*1024){
                    //10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body( "File is too large. Maximum size is 10MB");
                }
                String contentType=file.getContentType();
                //logger.info(contentType);
                //System.out.println(contentType);
                if(contentType==null || !contentType.startsWith("image/") ){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                String fileName= storeFile(file);
                if (index == 1) {
                    foundProduct.setThumbnail(fileName);
                    productService.updateProductThumbnail(id,foundProduct);

                    ;
                }
                //save db
                ProductImageDto newProductImageDto = ProductImageDto.builder()
                        .productId(foundProduct.getId())
                        //.imageUrl(fileName)
                        .build();
                productService.createProductImage(foundProduct.getId(), newProductImageDto);
                imageList.add(newProductImageDto);
                index++;
            }



            return ResponseEntity.ok().body(imageList);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(
            summary = "Create product without image"
    )
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto,
//                                           @RequestPart("file")MultipartFile file,
                                           @RequestHeader("Authorization") String token,
                                           BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                List<String>  errors =bindingResult.getFieldErrors().stream().map(e->e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }


            //save product db
            Product newProduct=productService.createProduct(productDto);
            return ResponseEntity.ok(newProduct);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @Operation(
            summary = "Create product with list of images"
    )
    @PostMapping(value="/products-upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createProductWithUploadImage(@Valid @RequestPart("product") MultipartFile productDto, @RequestPart("productImage") List<MultipartFile> files,
                                                          @RequestHeader("Authorization") String token,
//                                           @RequestPart("file")MultipartFile file,
                                          // @ModelAttribute("files") List<MultipartFile> files,
                                           BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream().map(e -> e.getDefaultMessage()).toList();
                return ResponseEntity.badRequest().body(errors);
            }
            //handle product
            //ObjectMapper mapper = new ObjectMapper();
            ProductDto uploadedProduct = objectMapper.readValue(productDto.getBytes(), ProductDto.class);

            //handle file
            files = files == null ? new ArrayList<MultipartFile>(): files;
            if (files.size() > 5) {
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images.");
            }
            List<ProductImageDto> imageList = new ArrayList<>();
            Long productId = null;
            int index = 1;
            for (MultipartFile file : files) {

                if (file.getSize() == 0) {
                    continue;
                }
                if (file.getSize() > 10 * 1024 * 1024) {
                    //10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large. Maximum size is 10MB");
                }
                String contentType = file.getContentType();
                System.out.println(contentType);
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                String fileName = storeFile(file);
                //logger.info(fileName);
                //set thumbnail first img
                if (index == 1) {
                   uploadedProduct.setThumbnail(fileName);
                    Product newProduct = productService.createProduct(uploadedProduct);
                    productId = newProduct.getId();
                    ;
                }

                //save product db

                index++;


                //save db
                if (productId != null) {
                    ProductImageDto newProductImageDto = ProductImageDto.builder()
                            .productId(productId)
                          //  .imageUrl(fileName)
                            .build();
                    productService.createProductImage(productId, newProductImageDto);
                    imageList.add(newProductImageDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Null Product Id");
                }


            }
            return ResponseEntity.ok(productId);
            //save product db
            //Product newProduct=productService.createProduct(productDto);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    private String storeFile(MultipartFile file) throws IOException{
        if(!isImageFile(file) || file.getOriginalFilename()==null){
            throw new IOException("Invalid image format.");
        }
        String fileName= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName= UUID.randomUUID().toString()+"_"+fileName;
        Path uploadDir= Paths.get("uploads");
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        Path des= Paths.get(uploadDir.toString(), uniqueFileName);

       // Files.copy(file.getInputStream(), des, StandardCopyOption.REPLACE_EXISTING);

        File targetFile = des.toFile();
        if (targetFile.exists()) {
            targetFile.delete();
        }
        Files.write(des,file.getBytes());
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType=file.getContentType();

        return contentType!=null && contentType.startsWith("image/");
    }
    @Operation(
            summary = "Update product by admin"
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") int id, @Valid @RequestBody ProductDto productDto, BindingResult bindingResult,  @RequestHeader("Authorization") String token){
        if(bindingResult.hasErrors()){
            List<String> errors= bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }
    @Operation(
            summary = "Delete product by admin"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int id, @RequestHeader("Authorization") String token){
        productService.deleteProduct(id);
        return ResponseEntity.ok("Delete products successfully");
    }
    @Operation(
            summary = "Fake data importing to db"
    )
    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts(){
        Faker faker= new Faker();
        for(int i=0; i<1_000; i++){
            String name= faker.commerce().productName();
            if(productService.existByName(name)){
                continue;
            }
            ProductDto productDto= ProductDto.builder()
                    .name(name)
                    .price((float)faker.number().numberBetween(0,1_300))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(2,5))
                    .build();
            productService.createProduct(productDto);

        }

        return ResponseEntity.ok("generate fake products successfully");
    }
    @Operation(
            summary = "Upload image and save in disk"
    )
    @PostMapping(value="/save-image/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadImage(@PathVariable("productId") Long productId,
                                              @RequestHeader("Authorization") String token,
                                              @RequestPart("files") MultipartFile[] files
                                              ) {
        try {
            for(MultipartFile f:files){
                String fileName= StringUtils.cleanPath(Objects.requireNonNull(f.getOriginalFilename()));
                String uniqueFileName= UUID.randomUUID().toString()+"_"+fileName;
                productService.saveImage(productId, f.getBytes(), uniqueFileName);
            }

            return new ResponseEntity<>("Image uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Optional<byte[]> getImageBlobByName(String name) {

        byte[] imageBlob = productService.getImageByName(name).getImageUrl();
        return Optional.ofNullable(imageBlob);

    }
    @Operation(
            summary = "Get image url"
    )
    @GetMapping("/images/{name}")
    public ResponseEntity<?> getImageProducts(@PathVariable String name) {
        try{
        Optional<byte[]> imageBlobOpt = getImageBlobByName(name);
        System.out.println(imageBlobOpt);
        if (imageBlobOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        byte[] imageBlob = imageBlobOpt.get();
        InputStream inputStream = new ByteArrayInputStream(imageBlob);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBlob.length);

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }catch (Exception e){
        return ResponseEntity.notFound().build();
    }
       // return null;
    }

}
