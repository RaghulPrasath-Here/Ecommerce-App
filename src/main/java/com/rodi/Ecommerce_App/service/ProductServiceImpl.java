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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepositories;
    @Autowired
    private CategoryRepository categoryRepositories;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;
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

    @Override
    public ProductResponse getCategoryProducts(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepositories.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepositories.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> products = productPage.getContent();

        if(products.size() == 0)    throw new APIException("No products available in this category");
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

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepositories.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<Product> products = productPage.getContent();

        if(products.size() == 0)   throw new APIException("Products not found with keyword: " + keyword);

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProducts(productDTOS);
        long totalPages = categoryRepositories.count() / pageSize;
        productResponse.setProducts(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product existingProduct = productRepositories.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepositories.save(existingProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
    @Override
    public ProductDTO deleteProduct(Long productId){
        Product existingProduct = productRepositories.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));
        productRepositories.delete(existingProduct);
        return modelMapper.map(existingProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepositories.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        String fileName = fileService.uploadImage(path, image);
        product.setImage(fileName);

        Product updatedProduct = productRepositories.save(product);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
