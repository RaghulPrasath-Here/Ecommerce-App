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

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProducts(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        return new ResponseEntity<>(productService.addProducts(productDTO, categoryId), HttpStatus.CREATED);
    }

}
