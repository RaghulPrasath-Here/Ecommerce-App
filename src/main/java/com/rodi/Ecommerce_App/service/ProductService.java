package com.rodi.Ecommerce_App.service;

import com.rodi.Ecommerce_App.payload.ProductDTO;
import com.rodi.Ecommerce_App.payload.ProductResponse;

public interface ProductService {

    ProductDTO addProducts(ProductDTO productDTO, Long categoryId);
}
