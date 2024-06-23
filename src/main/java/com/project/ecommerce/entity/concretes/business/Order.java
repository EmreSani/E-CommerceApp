package com.project.ecommerce.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.ecommerce.entity.concretes.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    private Double totalPrice;

    private String status; // "active", "cancelled", etc.

    private String anonymousIdentifier; // New field for anonymous users

    public void addOrderItem(OrderItem orderItemResponse) {
        orderItems.add(orderItemResponse);
        orderItemResponse.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItemResponse) {
        orderItems.remove(orderItemResponse);
        orderItemResponse.setOrder(null);
    }
}

