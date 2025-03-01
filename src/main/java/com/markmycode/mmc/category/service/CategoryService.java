package com.markmycode.mmc.category.service;

import com.markmycode.mmc.category.dto.CategoryResponseDto;
import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.category.repository.CategoryRepository;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 최상위 카테고리 조회
    public List<CategoryResponseDto> getParentCategories(){
        List<Category> parentCategories = categoryRepository.findByParentCategoryIsNull();
        return parentCategories.stream()
                .map(c -> new CategoryResponseDto(c.getCategoryName()))
                .collect(Collectors.toList());
    }

    // 특정 카테고리의 하위 카테고리 조회
    public List<CategoryResponseDto> getChildCategories(Integer parentCategoryId){
        // 부모 카테고리 유효성 검사
        getCategory(parentCategoryId);
        // 하위 카테고리 조회 후, DTO로 변환
        List<Category> childCategories = categoryRepository.findByParentCategory_CategoryId(parentCategoryId);
        // 부모 카테고리가 아닐 경우
        if (childCategories.isEmpty()){
            throw new NotFoundException(ErrorCode.INVALID_PARENT_CATEGORY);
        }
        return childCategories.stream()
                .map(c -> new CategoryResponseDto(c.getCategoryName()))
                .collect(Collectors.toList());
    }

    // 유효성 검사
    public void validateCategory(Integer parentCategoryId, Integer childCategoryId) {
        // 상위 카테고리 검증 (상위 카테고리가 제공된 경우)
        if(parentCategoryId != null && !categoryRepository.existsById(parentCategoryId)){
            throw new BadRequestException(ErrorCode.INVALID_CATEGORY);
        }
        // 하위 카테고리 검증 (하위 카테고리가 제공된 경우)
        if (childCategoryId != null){
            // 하위 카테고리 조회
            Category childCategory = getCategory(childCategoryId);
            // 하위 카테고리의 상위 카테고리 조회
            Category parentCategory = childCategory.getParentCategory();
            // 상위 카테고리가 제공되지 않았을 경우 : 조회한 상위 카테고리가 Null이면 안됨
            if (parentCategory == null) {
                throw new BadRequestException(ErrorCode.INVALID_CATEGORY);
            }
            // 상위 카테고리가 제공된 경우 : 조회한 상위 카테고리와 제공된 상위 카테고리가 일치해야함
            if (parentCategoryId != null && !childCategory.getParentCategory().getCategoryId().equals(parentCategoryId)) {
                throw new BadRequestException(ErrorCode.INVALID_CATEGORY);
            }
        }
    }

    // 해당 ID에 대한 엔티티 객체 반환
    public Category getCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_CATEGORY));
    }

}
