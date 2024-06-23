package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Order;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.exception.BadRequestException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderMappers;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.response.business.OrderResponse;

import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.repository.business.OrderRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.AccessDeniedException;
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
    private final ProductService productService;


    @Transactional
    public ResponseMessage<OrderResponse> createOrderFromCart(String username) {

        Cart cart = cartService.getCartByUsername(username);

        if (cart.getOrderItemList().isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.CART_IS_EMPTY);
        }

        Order order = new Order();
        order.setCustomer(userService.getUserByUserNameReturnsUser(username));
        order.setOrderDate(LocalDateTime.now());

        for (OrderItem orderItem : cart.getOrderItemList()) {
            order.addOrderItem(orderItem);   // Order entity'sine orderItemList ekleniyor
            orderItem.setOrder(order);       // OrderItem entity'sinin order alanı set ediliyor
            orderItem.setCart(null);
        }

        Order savedOrder = orderRepository.save(order);


        OrderResponse orderResponse = orderMapper.mapOrderToOrderResponse(savedOrder);

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



    @Transactional
    public OrderResponse deleteOrderById(Long orderId) {


        // Check if the order exists by its ID
        Order order = isOrderExistsById(orderId);

        // Ensure business logic allows deletion
        if (!canDeleteOrder(order)) {
            throw new BadRequestException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }
        // Removing the order from the customer's list of orders
        order.getCustomer().getOrders().remove(order);

        // Update stock quantities if necessary
        for (OrderItem item : order.getOrderItem()) {
            productService.updateProductStock(item.getProduct().getId(), item.getQuantity());
            orderItemService.deleteOrderItemByIdBeforeDeleteOrder(item.getId());
        }

        // The orphanRemoval = true annotation on Order ensures that OrderItems will be deleted
        orderRepository.delete(order);

        // Return the response after mapping the deleted order
        return orderMapper.mapOrderToOrderResponse(order);
    }

    private boolean canDeleteOrder(Order order) {
        // Implement business rules for order deletion

         return !order.getStatus().equals("shipped") && !order.getStatus().equals("delivered");

    }


    public OrderResponse cancelOrderById(Long orderId, String username) {
        Order order = isOrderExistsById(orderId);

        // Kullanıcının siparişini iptal etmesine izin ver
        if (!order.getCustomer().getUsername().equals(username)) {
            throw new AccessDeniedException("Bu siparişi iptal etme yetkiniz yok.");
        }

        //TODO: canCancelOrder methoduyla yeni kontroller eklenebilir.

        // Sipariş durumunu iptal edilmiş olarak güncelle
        order.setStatus("cancelled");

        // Sipariş içerisindeki her bir ürünün stok miktarını güncelle
        for (OrderItem item : order.getOrderItem()) { // Corrected to getOrderItems() method
            productService.updateProductStock(item.getProduct().getId(), item.getQuantity());
        }

        // Siparişi güncelle
        orderRepository.save(order);

        return orderMapper.mapOrderToOrderResponse(order);
    }

    public ResponseMessage<Page<OrderResponse>> getAllOrdersByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        Page<Order> ordersPage = orderRepository.findAll(pageable);
        Page<OrderResponse> orderResponsesPage = ordersPage.map(orderMapper::mapOrderToOrderResponse);

        return ResponseMessage.<Page<OrderResponse>>builder()
                .message(SuccessMessages.PRODUCTS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(orderResponsesPage)
                .build();
    }

    public ResponseMessage<Page<OrderResponse>> getAllOrdersByUserIdByPage(Long userId, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);

        Page<OrderResponse> orderResponsePage = orderPage.map(orderMapper::mapOrderToOrderResponse);

        return ResponseMessage.<Page<OrderResponse>>builder().message(SuccessMessages.ORDERS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(orderResponsePage)
                .build();

    }

    // Implement method to get orders by username
    public ResponseMessage<Page<OrderResponse>> getOrdersByUsername(String username, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        Page<Order> orderPage = orderRepository.findByUsername(username, pageable);

        Page<OrderResponse> orderResponsePage = orderPage.map(orderMapper::mapOrderToOrderResponse);

        return ResponseMessage.<Page<OrderResponse>>builder().message(SuccessMessages.ORDERS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(orderResponsePage)
                .build();
    }
}