package com.project.ecommerce.entity.concretes.business;

import lombok.*;

import javax.persistence.*;
import java.util.List;

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

    @Column(nullable = false)
    private Double stock;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItemList;

    //Order Item List: Maintained through a one-to-many relationship
    // with OrderItem, reflecting all instances where this product has been ordered.
}
