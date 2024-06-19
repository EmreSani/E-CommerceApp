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
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

//Product Relationship: Linked to Product through a many-to-one association, indicating the product included in the order item.
//Total Price Calculation: Handled by calculateTotalPrice method, ensuring totalPrice reflects the quantity and price of the associated product.


}
