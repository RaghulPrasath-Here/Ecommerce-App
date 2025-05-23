package com.rodi.Ecommerce_App.service;
import com.rodi.Ecommerce_App.payload.CategoryDTO;
import com.rodi.Ecommerce_App.payload.CategoryResponse;
public interface CategoryService {
    CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO CreateCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(long categoryId, CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(long categoryId);

}