package com.project.ecommerce.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<OrderItem> orderItemList;

}
