package com.project.ecommerce.entity.concretes.business;

import com.project.ecommerce.entity.concretes.user.User;
import lombok.*;

import javax.persistence.*;

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
    private Double totalPrice = quantity * product.getPrice();

    @ManyToOne
    private User user;

    @ManyToOne
    private Cart cart;

}
