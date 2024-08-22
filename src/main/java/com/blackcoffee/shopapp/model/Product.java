package com.blackcoffee.shopapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 350)
    private String name;
    private Float price;
    @Column(name = "thumbnail",  length = 350)
    private String thumbnail;
    private String description;
    @Column(name = "is_active")
    private int isActive;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;



}
