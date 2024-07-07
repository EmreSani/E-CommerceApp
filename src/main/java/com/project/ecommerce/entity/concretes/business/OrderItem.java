package com.project.ecommerce.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.entity.enums.OrderStatuses;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JsonBackReference
    private Product product;

    @Column(nullable = false)
    private Double totalPrice;

    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (product != null) {
            this.totalPrice = this.quantity * product.getPrice();
        }
    }

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private User customer;

    private OrderStatuses orderItemStatus; //sipariş öğesi hangi aşamada? sepette, sipariş edildi etc.

}
