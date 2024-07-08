package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Order;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.enums.OrderStatuses;
import com.project.ecommerce.entity.enums.SpecialTimes;
import com.project.ecommerce.exception.BadRequestException;
import com.project.ecommerce.exception.InvalidOrderStatusException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderMappers;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.business.OrderRequestForStatus;
import com.project.ecommerce.payload.response.business.OrderResponse;

import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.repository.business.OrderRepository;
import com.project.ecommerce.service.helper.BlackFridayCalculator;
import com.project.ecommerce.service.helper.HappyHourCalculator;
import com.project.ecommerce.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderMappers orderMapper;
    private final OrderItemService orderItemService;
    private final PageableHelper pageableHelper;
    private final ProductService productService;
    private final BlackFridayCalculator blackFridayCalculator;
    private final HappyHourCalculator happyHourCalculator;

    @Transactional
    public ResponseMessage<OrderResponse> createOrderFromCart(HttpServletRequest httpServletRequest) {

        String username = (String) httpServletRequest.getAttribute("username");
        Cart cart;
        if (username != null) {
            // Authenticated user, retrieve cart by username
            cart = cartService.getCartByUsername(username);
        } else {
            // Anonymous user, retrieve cart by session
            HttpSession session = httpServletRequest.getSession();
            cart = cartService.getCartBySession(session);
        }

        if (cart == null || cart.getOrderItemList().isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessages.CART_IS_EMPTY);
        }

        // Detach orderItems from cart to avoid shared references issue
        List<OrderItem> orderItems = new ArrayList<>(cart.getOrderItemList());

        Order order = Order.builder()
                .orderItems(new ArrayList<>(orderItems)) // Create a new list to avoid shared references
                .totalPrice(cart.getTotalPrice())
                .orderDate(LocalDateTime.now())
                .status(OrderStatuses.PREPARING_TO_SHIPPING)
                .build();

        // Özel zaman kontrolü
        LocalDateTime orderDate = order.getOrderDate();

        if (isSpecialTime(orderDate, SpecialTimes.BLACK_FRIDAY)) {
            // İndirim uygula (%20)
            double discountedPrice = blackFridayCalculator.applyDiscount(order.getTotalPrice(), 20);
            order.setTotalPrice(discountedPrice);
        } else if (isSpecialTime(orderDate, SpecialTimes.HAPPY_HOUR)) {
            // İndirim uygula (%10)
            double discountedPrice = happyHourCalculator.applyDiscount(order.getTotalPrice(), 10);
            order.setTotalPrice(discountedPrice);
        }

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            productService.updateProductStockForCreatingOrder(product.getId(), orderItem.getQuantity());
            orderItem.setCart(null);
//            order.addOrderItem(orderItem);   // Order entity'sine orderItemList ekleniyor
            orderItem.setOrder(order);      // OrderItem entity'sinin order alanı set ediliyor
            orderItem.setOrderItemStatus(OrderStatuses.ORDERED);
        }


        if (cart.getUser() != null) {
            order.setCustomer(cart.getUser());
        } else {
            order.setAnonymousIdentifier(httpServletRequest.getSession().getId()); // Set the session ID for anonymous users
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart items and recalculate total price
        cart.getOrderItemList().clear();
        cart.recalculateTotalPrice();

        // Save updated cart
        cartService.saveCart(cart);

        OrderResponse orderResponse = orderMapper.mapOrderToOrderResponse(savedOrder);

        return ResponseMessage.<OrderResponse>builder()
                .message(String.format(SuccessMessages.ORDER_CREATE, order.getId()))
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

        // Ensure business logic allows deletion. Order status canceled ise silebiliyoruz onun haricinde order kayıtlarını tutmak istiyoruz.
        if (!canDeleteOrder(order)) {
            throw new BadRequestException(ErrorMessages.ORDER_CAN_NOT_BE_DELETED);
        }
        // Removing the order from the customer's list of orders
       if (order.getCustomer()!=null) {
           order.getCustomer().getOrders().remove(order);
       }

        // Update stock quantities if necessary
        if (!order.getStatus().equals(OrderStatuses.CANCELED)) {
            for (OrderItem item : order.getOrderItems()) {
                orderItemService.deleteOrderItemByIdBeforeDeleteOrder(item.getId());
            }
        }

        // The orphanRemoval = true annotation on Order ensures that OrderItems will be deleted
        orderRepository.delete(order);

        // Return the response after mapping the deleted order
        return orderMapper.mapOrderToOrderResponse(order);
    }

    private boolean canDeleteOrder(Order order) {
        // Implement business rules for order deletion
        return order.getStatus().equals(OrderStatuses.CANCELED);
    }

    private void canCancelOrder(Order order) {
        // Implement business rules for order cancelation, we can add more rules.

        if (order.getStatus().equals(OrderStatuses.SHIPPED) || order.getStatus().equals(OrderStatuses.DELIVERED)) {
            throw new BadRequestException(ErrorMessages.ORDER_CAN_NOT_BE_CANCELED);
        }

    }


    public OrderResponse cancelOrderById(Long orderId, HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        Order order = isOrderExistsById(orderId);

        // Kullanıcının siparişini iptal etmesine izin ver
        if (!order.getCustomer().getUsername().equals(username)) {
            throw new AccessDeniedException(ErrorMessages.ORDER_CAN_NOT_BE_CANCELED);
        }

        // Siparişin durumuna göre iptal kontrolü
        canCancelOrder(order);

        // Sipariş durumunu iptal edilmiş olarak güncelle
        order.setStatus(OrderStatuses.CANCELED);

        // Sipariş içerisindeki her bir ürünün stok miktarını güncelle
        for (OrderItem item : order.getOrderItems()) { // Corrected to getOrderItems() method
            productService.updateProductStockForCancellingOrder(item.getProduct().getId(), item.getQuantity());
            item.setOrderItemStatus(OrderStatuses.CANCELED);
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

    public ResponseMessage<OrderResponse> updateOrderStatus(OrderRequestForStatus orderRequestForStatus, Long orderId) {

        Order order = isOrderExistsById(orderId);

        OrderStatuses status;
        try {
            status = OrderStatuses.fromString(orderRequestForStatus.getStatus());
        } catch (InvalidOrderStatusException e) {
            return ResponseMessage.<OrderResponse>builder().message(e.getMessage())
                    .httpStatus(HttpStatus.BAD_REQUEST).build();
        }

        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);

        return ResponseMessage.<OrderResponse>builder().message(SuccessMessages.ORDER_UPDATE).httpStatus(HttpStatus.OK)
                .object(orderMapper.mapOrderToOrderResponse(updatedOrder)).build();
    }

    public OrderResponse cancelAnonymousOrderById(Long orderId, HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession();
        Order order = isOrderExistsById(orderId);

        if (order.getCustomer()!=null){
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
        // Oturumdan anonim tanımlayıcıyı al
        String sessionAnonymousIdentifier = session.getId();

        if (sessionAnonymousIdentifier == null) {
            throw new AccessDeniedException("No anonymous identifier found in session.");
        }

//        // Kullanıcının siparişini iptal etmesine izin ver
//        if (!order.getAnonymousIdentifier().equals(sessionAnonymousIdentifier)) {
//            throw new AccessDeniedException(ErrorMessages.ORDER_CAN_NOT_BE_CANCELED);
//        }

        // Null kontrolü ekleyin
        String orderAnonymousIdentifier = order.getAnonymousIdentifier();
        if (orderAnonymousIdentifier == null || !orderAnonymousIdentifier.equals(sessionAnonymousIdentifier)) {
            throw new AccessDeniedException(ErrorMessages.ORDER_CAN_NOT_BE_CANCELED);
        }

        // Siparişin durumuna göre iptal kontrolü
        canCancelOrder(order);

        // Sipariş durumunu iptal edilmiş olarak güncelle
        order.setStatus(OrderStatuses.CANCELED);

        // Sipariş içerisindeki her bir ürünün stok miktarını güncelle
        for (OrderItem item : order.getOrderItems()) { // Corrected to getOrderItems() method
            productService.updateProductStockForCancellingOrder(item.getProduct().getId(), item.getQuantity());
            item.setOrderItemStatus(OrderStatuses.CANCELED);
        }

        // Siparişi güncelle
        orderRepository.save(order);

        return orderMapper.mapOrderToOrderResponse(order);
    }

    // SpecialTimes enumunu ve indirim hesaplama metodunu kullanan özel zaman kontrolü
    private boolean isSpecialTime(LocalDateTime orderDate, SpecialTimes specialTime) {
        switch (specialTime) {
            case BLACK_FRIDAY:
                LocalDate blackFriday = blackFridayCalculator.calculateBlackFriday();
                LocalDate localDate = orderDate.toLocalDate();
                return localDate.equals(blackFriday);
            case HAPPY_HOUR:
                return happyHourCalculator.isHappyHour(orderDate);
            default:
                return false;
        }
    }
}