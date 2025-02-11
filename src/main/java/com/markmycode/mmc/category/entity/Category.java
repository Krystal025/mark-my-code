package com.markmycode.mmc.category.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false, length = 50)
    private String categoryName;

    // @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // 외래 키(FK): 같은 테이블(category_id) 참조
    private Category parentCategory;

//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
//    private List<Category> children; // 하위 카테고리 리스트
}
