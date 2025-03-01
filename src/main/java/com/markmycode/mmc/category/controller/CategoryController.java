package com.markmycode.mmc.category.controller;

import com.markmycode.mmc.category.dto.CategoryResponseDto;
import com.markmycode.mmc.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 상위 카테고리 목록
    @GetMapping
    public List<CategoryResponseDto> getParentCategories(){
        return categoryService.getParentCategories();
    }

    // 하위 카테고리 목록
    @GetMapping("/{parentCategoryId}")
    public List<CategoryResponseDto> getChildCategories(@PathVariable("parentCategoryId") Integer parentCategoryId){
        return categoryService.getChildCategories(parentCategoryId);
    }

}
