package com.project.ecommerce.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<OrderItem> orderItemList;

    private Double totalPrice;

    @OneToOne
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Order> orders;

    private String sessionId; // Anonim kullanıcılar için sessionId

    // Method to recalculate total price based on order items
    public void recalculateTotalPrice() {
        if (orderItemList != null && !orderItemList.isEmpty()) {
            this.totalPrice = orderItemList.stream()
                    .mapToDouble(OrderItem::getTotalPrice)
                    .sum();
        } else {
            this.totalPrice = 0.0; // Set to 0 if no items in cart
        }
    }

}
