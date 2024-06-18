package com.project.ecommerce.payload.request.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Order;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.concretes.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @NotNull(message = "Cart ID cannot be null")
    private Long cartId;

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;
}
