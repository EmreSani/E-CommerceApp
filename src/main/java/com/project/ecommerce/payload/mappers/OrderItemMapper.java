package com.project.ecommerce.payload.mappers;

import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    private final UserMapper userMapper;

    public OrderItemResponse mapOrderItemToOrderItemResponse (OrderItem orderItemResponse){
        return OrderItemResponse.builder()
                .totalPrice(orderItemResponse.getTotalPrice())
                .id(orderItemResponse.getId())
                .quantity(orderItemResponse.getQuantity())
              //  .userId(orderItemResponse.getCustomer().getId())
                .productId(orderItemResponse.getProduct().getId())
             //   .cartId(orderItemResponse.getCart().getId())
                .build();
    }

}
