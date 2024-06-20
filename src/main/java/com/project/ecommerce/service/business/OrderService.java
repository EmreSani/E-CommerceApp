package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Order;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderMappers;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.response.business.OrderResponse;
import com.project.ecommerce.payload.response.business.ProductResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.repository.business.OrderRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final OrderMappers orderMapper;
    private final OrderItemService orderItemService;
    private final PageableHelper pageableHelper;


    @Transactional
    public ResponseMessage<OrderResponse> createOrderFromCart(String username) {

        Cart cart = cartService.getCartByUsername(username);

        if (cart.getOrderItemList().isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.CART_IS_EMPTY);
        }

        Order order = new Order();
        order.setCustomer(userService.getUserByUserNameReturnsUser(username));
        order.setOrderDate(LocalDateTime.now());
        order.setCart(cart);  // Sepeti siparişe bağla

        for (OrderItem orderItem : cart.getOrderItemList()) {
            order.addOrderItem(orderItem);   // Order entity'sine orderItemList ekleniyor
            orderItem.setOrder(order);       // OrderItem entity'sinin order alanı set ediliyor
            Double stock = orderItem.getProduct().getStock();
            Double newStock = stock - orderItem.getQuantity();
            orderItem.getProduct().setStock(newStock);
        }

        orderRepository.save(order);


        OrderResponse orderResponse = orderMapper.mapOrderToOrderResponse(order);

        cartService.clearCart(cart);
        cart.recalculateTotalPrice();

        return ResponseMessage.<OrderResponse>builder()
                .message(String.format(SuccessMessages.USER_CREATE, order.getId()))
                .httpStatus(HttpStatus.OK)
                .object(orderResponse)
                .build();
    }

    public Order isOrderExistsById(Long id) {

        return orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND_USER_MESSAGE,
                        id)));
    }


    public OrderResponse deleteOrderById(Long orderId) {

        Order order = isOrderExistsById(orderId);

        orderRepository.delete(order);

        return orderMapper.mapOrderToOrderResponse(order);

    }

    public ResponseMessage<Page<OrderResponse>> getAllOrdersByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        return ResponseMessage.<Page<OrderResponse>>builder()
                .message(SuccessMessages.PRODUCTS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(orderRepository.findAll(pageable).map(orderMapper::mapOrderToOrderResponse))
                .build();
    }
}