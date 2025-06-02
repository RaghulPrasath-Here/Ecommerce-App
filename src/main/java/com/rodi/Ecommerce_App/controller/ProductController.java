package com.rodi.Ecommerce_App.controller;

import com.rodi.Ecommerce_App.config.AppConstants;
import com.rodi.Ecommerce_App.payload.ProductDTO;
import com.rodi.Ecommerce_App.payload.ProductResponse;
import com.rodi.Ecommerce_App.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProducts(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        return new ResponseEntity<>(productService.addProducts(productDTO, categoryId), HttpStatus.CREATED);
    }

    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.sortOrder, required = false) String sortOrder){
        return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }
    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getCategoryProducts(@PathVariable Long categoryId,
           @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
           @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
           @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
           @RequestParam(name = "sortOrder", defaultValue = AppConstants.sortOrder, required = false) String sortOrder){
        return new ResponseEntity<>(productService.getCategoryProducts(categoryId, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.sortOrder, required = false) String sortOrder){
        return new ResponseEntity<>(productService.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.FOUND);
    }

    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@Valid @PathVariable Long productId){
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }

    @PutMapping("products/{productId}/image")
    public ResponseEntity<ProductDTO> updateImage(@PathVariable Long productId, @RequestParam MultipartFile image) throws IOException {
        return new ResponseEntity<>(productService.updateImage(productId, image), HttpStatus.OK);
    }
}
