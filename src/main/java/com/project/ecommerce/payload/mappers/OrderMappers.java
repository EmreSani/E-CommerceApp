package com.project.ecommerce.payload.mappers;

import com.project.ecommerce.entity.concretes.business.Order;

import com.project.ecommerce.payload.response.business.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMappers {

    private final OrderItemMapper orderItemMapper;

    public OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setOrderStatus(orderResponse.getOrderStatus());
        orderResponse.setOrderItems(order.getOrderItems().stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList()));
        orderResponse.setOrderStatus(order.getStatus());
        return orderResponse;
    }
}
