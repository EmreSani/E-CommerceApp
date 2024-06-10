package com.project.ecommerce.entity.business;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "t_product")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String productName;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Double price;
}
