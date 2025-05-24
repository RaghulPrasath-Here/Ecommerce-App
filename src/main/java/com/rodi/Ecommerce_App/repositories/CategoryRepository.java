package com.rodi.Ecommerce_App.repositories;

import com.rodi.Ecommerce_App.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

        Category findByCategoryName(String categoryName);

        }