package com.markmycode.mmc.platform.entity;

import jakarta.persistence.*;

@Entity
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer platformId;

    @Column(nullable = false, length = 50)
    private String platformName;

}
