package com.rodi.Ecommerce_App.controller;

import com.rodi.Ecommerce_App.payload.CategoryDTO;
import com.rodi.Ecommerce_App.payload.CategoryResponse;
import com.rodi.Ecommerce_App.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategory(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.sortBy, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.sortOrder, required = false) String sortOrder) {
        CategoryResponse categoryResponse = categoryService.getAllCategory(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> CreateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.CreateCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> UpdateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
    }
    @DeleteMapping("/admin/categories/{categoryid}")
    public ResponseEntity<CategoryDTO> DeleteCategory(@PathVariable Long categoryid) {
        CategoryDTO deleteCategoryDTO = categoryService.deleteCategory(categoryid);
        return new ResponseEntity<>(deleteCategoryDTO, HttpStatus.OK);
    }
}
