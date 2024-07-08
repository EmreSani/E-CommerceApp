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

    public OrderItemResponse mapOrderItemToOrderItemResponse (OrderItem orderItem){
        return OrderItemResponse.builder()
                .totalPrice(orderItem.getTotalPrice())
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
              //  .userId(orderItem.getCustomer().getId())
                .productId(orderItem.getProduct().getId())
             //   .cartId(orderItem.getCart().getId())
                .orderItemStatus(orderItem.getOrderItemStatus().name())
                .build();
    }

}
