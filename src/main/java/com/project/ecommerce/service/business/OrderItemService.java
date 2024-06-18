package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Order;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.payload.mappers.OrderItemMapper;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.repository.business.OrderItemRepository;
import com.project.ecommerce.repository.business.OrderRepository;
import com.project.ecommerce.service.helper.MethodHelper;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final MethodHelper methodHelper;
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;


    public List<com.project.ecommerce.payload.response.business.OrderItemResponse> getOrderItemsByUserId(Long userId) {

        List<OrderItem> orderItemResponseList = orderItemRepository.findAllByUserId(userId);

        return orderItemResponseList.stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());

    }

    public OrderItem createOrderItem(OrderItemRequest orderItemRequest) {

        Product product = productService.isProductExistsById(orderItemRequest.getProductId());
        User customer = methodHelper.isUserExist(orderItemRequest.getCustomerId());
        Cart cart = cartService.getCartByUsername(customer.getUsername());
        Order order = orderService.isOrderExistsById(orderItemRequest.getOrderId());

        Double totalPrice = orderItemRequest.getQuantity() * product.getPrice();

        OrderItem orderItem = OrderItem.builder()
                .quantity(orderItemRequest.getQuantity())
                .product(product)
                .totalPrice(totalPrice)
                .cart(cart)
                .order(order)
                .customer(customer)
                .build();

        return orderItemRepository.save(orderItem);


    }
}
