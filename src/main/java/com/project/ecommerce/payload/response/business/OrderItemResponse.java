package com.project.ecommerce.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Product;

import com.project.ecommerce.payload.response.user.UserResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {
    private Long id;

    private Integer quantity;

    private Product product;
    private Double totalPrice;
    private UserResponse userResponse;
    private Cart cart;
}
