package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.enums.OrderStatuses;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderItemMapper;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.payload.request.business.OrderItemRequestForUpdate;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.repository.business.OrderItemRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final PageableHelper pageableHelper;

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

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

        // Create the OrderItem
        OrderItem orderItem = OrderItem.builder()
                .quantity(orderItemRequest.getQuantity())
                .product(product)
                .totalPrice(orderItemRequest.getQuantity() * product.getPrice())
                .cart(customersCart)
                //   .customer(customer) //if customer doesnt exists?
                .build();

        orderItem.setOrderItemStatus(OrderStatuses.IN_CART);
        // Add the OrderItem to the Cart's orderItemList
        customersCart.getOrderItemList().add(orderItem);

        // Recalculate the Cart's total price
        customersCart.recalculateTotalPrice();

        // Save the OrderItem (This will cascade save to Cart as well due to CascadeType.ALL)
        logger.info("Saving OrderItem: {}", orderItem);
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
        logger.info("Updating quantity of OrderItem name: {} to {}", orderItem.getId(), orderItem.getQuantity());

        // Save the updated OrderItem
        //  logger.info("Updating OrderItem: {}", orderItem);
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

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

        Cart customersCart;
        if (username != null) {
            // Authenticated user, retrieve cart by username
            customersCart = cartService.getCartByUsername(username);
        } else {
            // Anonymous user, retrieve cart by session
            HttpSession session = httpServletRequest.getSession();
            customersCart = cartService.getCartBySession(session);
        }

        customersCart.getOrderItemList().remove(orderItem);
        customersCart.recalculateTotalPrice();
        if (username != null) {
            cartService.saveCart(customersCart);
        }
        logger.info("Deleting OrderItem: {}", orderItem);
        orderItemRepository.delete(orderItem);
        return orderItemMapper.mapOrderItemToOrderItemResponse(orderItem);

    }

    public void deleteOrderItemByIdBeforeDeleteOrder(Long orderItemId) { //without httpservlet

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

//        Product product = orderItem.getProduct();
//        product.setStock(product.getStock() + orderItem.getQuantity());
//        logger.info("Deleting OrderItems: {}", orderItem);
//        Bu kontrolleri Product service içerisinde updateProductStockForCancellingOrder methodunda yaptığımız için
//        burda yapmıyoruz. yoksa order iptal edilince elimizdeki ürün stoğu 2 kez arttırılmış oluyor.
        orderItemRepository.delete(orderItem);

    }

//    public ResponseMessage<List<OrderItemResponse>> getUsersOrderItemsById(Long userId) {
//
//        List<OrderItemResponse> orderItemList = getOrderItemsByUserId(userId);
//
//        return ResponseMessage.<List<OrderItemResponse>>builder()
//                .message(SuccessMessages.ORDER_ITEMS_FOUND)
//                .httpStatus(HttpStatus.OK)
//                .object(orderItemList)
//                .build();
//    }

    public List<OrderItemResponse> getOrderItemsByUserId(Long userId) {

        List<OrderItem> orderItemResponseList = orderItemRepository.findAllByUserId(userId);

        return orderItemResponseList.stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());

    }
}
