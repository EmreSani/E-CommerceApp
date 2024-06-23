package com.project.ecommerce.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Product;

import com.project.ecommerce.payload.response.user.UserResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {
    private Long id;
    private Integer quantity;
    private Long productId;
    private Double totalPrice;
    private Long userId;
    private Long cartId;

}
