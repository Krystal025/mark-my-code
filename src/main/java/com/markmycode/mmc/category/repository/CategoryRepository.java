package com.markmycode.mmc.category.repository;

import com.markmycode.mmc.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // 최상위 카테고리 조회
    List<Category> findByParentCategoryIsNull();

    // 특정 부모 카테고리에 속하는 하위 카테고리 조회
    List<Category> findByParentCategory(Category parentCategory);

    // 부모 카테고리 ID를 자식 카테고리 ID로 조회하는 메소드
    Integer findParentIdByCategoryId(Integer categoryId);

}
