package com.rodi.Ecommerce_App.service;

import com.rodi.Ecommerce_App.exception.APIException;
import com.rodi.Ecommerce_App.exception.ResourceNotFoundException;
import com.rodi.Ecommerce_App.model.Category;
import com.rodi.Ecommerce_App.payload.CategoryDTO;
import com.rodi.Ecommerce_App.payload.CategoryResponse;
import com.rodi.Ecommerce_App.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepositories;
    @Autowired
    private ModelMapper modelMapper;
    public CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepositories.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()){
            throw new APIException("No Products created");
        }
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        long totalPages = categoryRepositories.count() / pageSize;
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    public CategoryDTO CreateCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryfromDb = categoryRepositories.findByCategoryName(category.getCategoryName());
        if(categoryfromDb != null)   throw new APIException("Category with the name "+ categoryDTO.getCategoryName() + " already exists");
        Category savedCategory = categoryRepositories.save(category);
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Category existCategory = categoryRepositories.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId", categoryId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        existCategory = categoryRepositories.save(category);

        return modelMapper.map(existCategory, CategoryDTO.class);

    }
    public CategoryDTO deleteCategory(long categoryId) {
        Category deleteCategory = categoryRepositories.findById(categoryId)
                .orElseThrow( () -> new ResourceNotFoundException("Category","CategoryId", categoryId));
        categoryRepositories.deleteById(categoryId);
        return modelMapper.map(deleteCategory, CategoryDTO.class);
    }


}

