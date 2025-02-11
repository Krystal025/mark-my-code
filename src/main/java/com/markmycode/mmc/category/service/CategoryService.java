package com.markmycode.mmc.category.service;

import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 최상위 카테고리 조회
    public List<Category> getRootCategoryList(){
        return categoryRepository.findByParentCategoryIsNull();
    }

    // 특정 카테고리의 하위 카테고리 조회
    public List<Category> getSubCategorieList(Integer parentId){
        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        return categoryRepository.findByParentCategory(parentCategory);    }

}
