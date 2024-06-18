package com.project.ecommerce.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.ecommerce.entity.concretes.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItemList = new ArrayList<>();

    private Double totalPrice;

    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL)
    @JsonBackReference
    private User user;

    @OneToOne(mappedBy = "cart")
    private Order order;  // Sipariş ilişkisi

    public void recalculateTotalPrice() {
        totalPrice = orderItemList.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
}
