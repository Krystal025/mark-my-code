package com.markmycode.mmc.language.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problem_language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer languageId;

    @Column(nullable = false, length = 50)
    private String prismName;

    @Column(nullable = false, length = 50)
    private String displayName;

}
