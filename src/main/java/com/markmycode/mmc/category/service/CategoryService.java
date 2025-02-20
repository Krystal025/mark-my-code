package com.markmycode.mmc.category.service;

import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.category.repository.CategoryRepository;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 최상위 카테고리 조회
    public List<Category> getParentCategoryList(){
        return categoryRepository.findByParentCategoryIsNull();
    }

    // 특정 카테고리의 하위 카테고리 조회
    public List<Category> getChildCategoryList(Integer parentCategoryId){
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryRepository.findByParentCategory(parentCategory);    }

    // 카테고리 유효성 검증
    public void validateCategory(Integer parentCategoryId, Integer childCategoryId) {
        // 상위 카테고리 검증 (상위 카테고리가 제공된 경우)
        if(parentCategoryId != null && !categoryRepository.existsById(parentCategoryId)){
                throw new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // 하위 카테고리 검증 (하위 카테고리가 제공된 경우)
        if (childCategoryId != null){
            // 하위 카테고리 조회
            Category childCategory = categoryRepository.findById(childCategoryId)
                    .orElseThrow(()-> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
            // 하위 카테고리의 상위 카테고리 조회
            Category parentCategory = childCategory.getParentCategory();
            // 상위 카테고리가 제공되지 않았을 경우 : 조회한 상위 카테고리가 Null이면 안됨
            if (parentCategory == null) {
                throw new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            // 상위 카테고리가 제공된 경우 : 조회한 상위 카테고리와 제공된 상위 카테고리가 일치해야함
            if (parentCategoryId != null && !childCategory.getParentCategory().getCategoryId().equals(parentCategoryId)) {
                throw new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND);
            }
        }
    }

}
