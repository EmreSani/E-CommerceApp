package com.project.ecommerce.entity.concretes.business;

import com.project.ecommerce.entity.concretes.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItem = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    public void addOrderItem(OrderItem orderItemResponse) {
        orderItem.add(orderItemResponse);
        orderItemResponse.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItemResponse) {
        orderItem.remove(orderItemResponse);
        orderItemResponse.setOrder(null);
    }
}

