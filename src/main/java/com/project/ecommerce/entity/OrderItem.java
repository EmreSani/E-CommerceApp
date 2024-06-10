package com.project.ecommerce.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_orderitem")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
 // @PrePersist?
    private Integer totalPrice;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Cart cart;

}
