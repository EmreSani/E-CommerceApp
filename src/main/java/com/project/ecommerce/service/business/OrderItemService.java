package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.payload.mappers.OrderItemMapper;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.repository.business.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    public List<OrderItemResponse> getOrderItemsByUserId(Long userId) {

       List<OrderItem> orderItemList = orderItemRepository.findAllByUserId(userId);

        return orderItemList.stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());

    }
}
