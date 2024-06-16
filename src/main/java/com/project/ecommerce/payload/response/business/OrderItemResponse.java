package com.project.ecommerce.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Product;

import com.project.ecommerce.payload.response.user.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {
    private Long id;

    private Integer quantity;

    private Product product;
    private Double totalPrice;
    private UserResponse userResponse;
    private Cart cart;
}
