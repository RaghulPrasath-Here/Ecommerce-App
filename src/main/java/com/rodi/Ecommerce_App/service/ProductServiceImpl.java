package com.rodi.Ecommerce_App.service;

import com.rodi.Ecommerce_App.exception.APIException;
import com.rodi.Ecommerce_App.exception.ResourceNotFoundException;
import com.rodi.Ecommerce_App.model.Category;
import com.rodi.Ecommerce_App.model.Product;
import com.rodi.Ecommerce_App.payload.ProductDTO;
import com.rodi.Ecommerce_App.payload.ProductResponse;
import com.rodi.Ecommerce_App.repositories.CategoryRepository;
import com.rodi.Ecommerce_App.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepositories;
    @Autowired
    private CategoryRepository categoryRepositories;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ProductDTO addProducts(ProductDTO productDTO, Long categoryId) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product productFromDb = productRepositories.findByProductName(product.getProductName());
        if(productFromDb != null)   throw new APIException(productDTO.getProductName() + "already exists");
        Category productCategory = categoryRepositories.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId", categoryId));
        product.setImage("defualt.png");
        product.setCategory(productCategory);
        double specialprice = product.getPrice() -
                ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialprice);
        Product savedProduct = productRepositories.save(product);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepositories.findAll(pageDetails);
        List<Product> products = productPage.getContent();

        if (products.isEmpty()){
            throw new APIException("No Products created");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        long totalPages = categoryRepositories.count() / pageSize;
        productResponse.setProducts(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

}
