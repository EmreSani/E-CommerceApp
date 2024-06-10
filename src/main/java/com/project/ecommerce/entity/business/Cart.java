package com.project.ecommerce.entity.business;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_cart")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Cart {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<OrderItem> orderItems;

    private Integer totalPrice;
}
