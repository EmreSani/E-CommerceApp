package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderItemMapper;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.payload.request.business.OrderItemRequestForUpdate;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.repository.business.CartRepository;
import com.project.ecommerce.repository.business.OrderItemRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final CartService cartService;
    private final ProductService productService;
    @Autowired
    private final UserService userService;
    private final PageableHelper pageableHelper;
    private final CartRepository cartRepository;


    public List<OrderItemResponse> getOrderItemsByUserId(Long userId) {

        List<OrderItem> orderItemResponseList = orderItemRepository.findAllByUserId(userId);

        return orderItemResponseList.stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());

    }


    @Transactional
    public ResponseEntity<OrderItemResponse> createOrderItem(OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {

        String username = (String) httpServletRequest.getAttribute("username");

        // Find the product by ID
        Product product = productService.isProductExistsById(orderItemRequest.getProductId());

        // Check if there is sufficient stock
        if (product.getStock() < orderItemRequest.getQuantity()) {
            throw new ResourceNotFoundException("Insufficient stock for product: " + product.getProductName());
        }

        Cart customersCart;
        if (username != null) {
            // Authenticated user, retrieve cart by username
            customersCart = cartService.getCartByUsername(username);
        } else {
            // Anonymous user, retrieve cart by session
            HttpSession session = httpServletRequest.getSession();
            customersCart = cartService.getCartBySession(session);
        }

//        User customer = userService.getUserByUserNameReturnsUser(username);
//        Cart customersCart = customer.getCart();

        // Create the OrderItem
        OrderItem orderItem = OrderItem.builder()
                .quantity(orderItemRequest.getQuantity())
                .product(product)
                .totalPrice(orderItemRequest.getQuantity() * product.getPrice())
                .cart(customersCart)
                .customer(customer)
                .build();

        // Add the OrderItem to the Cart's orderItemList
        customersCart.getOrderItemList().add(orderItem);

        // Recalculate the Cart's total price
        customersCart.recalculateTotalPrice();

        // Save the OrderItem (This will cascade save to Cart as well due to CascadeType.ALL)
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Map and return the response
        return ResponseEntity.ok(orderItemMapper.mapOrderItemToOrderItemResponse(savedOrderItem));
    }


    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemRepository.findAll().stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());
    }

    public OrderItemResponse getOrderItemById(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

        return orderItemMapper.mapOrderItemToOrderItemResponse(orderItem);

    }

    @Transactional
    public OrderItemResponse updateOrderItem(OrderItemRequestForUpdate orderItemRequestForUpdate, HttpServletRequest httpServletRequest, Long orderItemId) {
        String username = (String) httpServletRequest.getAttribute("username");

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));
        // Ürünü alın
        Product product = productService.isProductExistsById(orderItem.getProduct().getId());

        // Stok kontrolü
        if (product.getStock() < orderItemRequestForUpdate.getQuantity()) {
            throw new ResourceNotFoundException("Insufficient stock for product: " + product.getProductName());
        }

            // Update order item quantity
            orderItem.setQuantity(orderItemRequestForUpdate.getQuantity());

            // Save the updated OrderItem
            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

            // Retrieve or create the Cart based on user authentication
            Cart cart;
            if (username != null) {
                cart = cartService.getCartByUsername(username);
            } else {
                HttpSession session = httpServletRequest.getSession();
                cart = cartService.getCartBySession(session);
            }

            // Recalculate total price of the Cart
            cart.recalculateTotalPrice();

            // Save the updated Cart
            cartService.saveCart(cart);

            // Return mapped response for the updated OrderItem
            return orderItemMapper.mapOrderItemToOrderItemResponse(updatedOrderItem);

    }

    public ResponseMessage<Page<OrderItemResponse>> getAllOrderItemsByPage(int page, int size, String sort, String
            type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        return ResponseMessage.<Page<OrderItemResponse>>builder()
                .message(SuccessMessages.PRODUCTS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(orderItemRepository.findAll(pageable).map(orderItemMapper::mapOrderItemToOrderItemResponse))
                .build();
    }

    @Transactional
    public OrderItemResponse deleteOrderItemById(Long orderItemId, HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");

        Cart cart;

        // Ürünü alın
        //    Product product = productService.isProductExistsById(orderItemRequestForUpdate.getProductId()); bu kontrole gerek yok

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

        Product product = orderItem.getProduct();
        product.setStock(product.getStock() + orderItem.getQuantity());

        if (username != null) {

            cart = cartService.getCartByUsername(username);
            cart.getOrderItemList().remove(orderItem);
            cart.recalculateTotalPrice();
            cartService.saveCart(cart);


        } else {
            HttpSession session = httpServletRequest.getSession();
            cart = cartService.getCartBySession(session);
            cart.getOrderItemList().remove(orderItem);
            cart.recalculateTotalPrice();

            // Sipariş öğesini silin (anonim kullanıcı için müşteri olmadan)

        }

        orderItemRepository.delete(orderItem);
        return orderItemMapper.mapOrderItemToOrderItemResponse(orderItem);

    }

    public void deleteOrderItemByIdBeforeDeleteOrder(Long orderItemId) { //without httpservlet

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

        Product product = orderItem.getProduct();
        product.setStock(product.getStock() + orderItem.getQuantity());
        orderItemRepository.delete(orderItem);

    }

    public ResponseMessage<List<OrderItemResponse>> getUsersOrderItemsById(Long userId) {

        List<OrderItemResponse> orderItemList = getOrderItemsByUserId(userId);

        return ResponseMessage.<List<OrderItemResponse>>builder()
                .message(SuccessMessages.ORDER_ITEMS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(orderItemList)
                .build();
    }
}
