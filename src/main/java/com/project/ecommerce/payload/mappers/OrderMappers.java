package com.project.ecommerce.payload.mappers;

import com.project.ecommerce.entity.concretes.business.Order;

import com.project.ecommerce.payload.response.business.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMappers {

    private final OrderItemMapper orderItemMapper;

    public OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setCustomerId(order.getCustomer().getId());
        orderResponse.getOrderItems().addAll(order.getOrderItem().stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).toList());
        orderResponse.setCartId(order.getCart().getId());

        return orderResponse;
    }
}
