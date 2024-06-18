package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderItemMapper;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.payload.request.business.OrderItemRequestForUpdate;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.repository.business.OrderItemRepository;
import com.project.ecommerce.service.helper.MethodHelper;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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


    public List<OrderItemResponse> getOrderItemsByUserId(Long userId) {

        List<OrderItem> orderItemResponseList = orderItemRepository.findAllByUserId(userId);

        return orderItemResponseList.stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());

    }

    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {

        String username = (String) httpServletRequest.getAttribute("username");

        Cart cart;

        // Ürünü alın
        Product product = productService.isProductExistsById(orderItemRequest.getProductId());

        // Stok kontrolü
        if (product.getStock() < orderItemRequest.getQuantity()) {
            throw new ResourceNotFoundException("Insufficient stock for product: " + product.getProductName());
        }

        if (username != null) {
            User customer = userService.getUserByUserNameReturnsUser(username);
            cart = cartService.getCartByUsername(username);

            // Sipariş öğesini oluşturun ve kaydedin
            OrderItem orderItem = OrderItem.builder()
                    .quantity(orderItemRequest.getQuantity())
                    .product(product)
                    .totalPrice(orderItemRequest.getQuantity() * product.getPrice())
                    .cart(cart)
                    .customer(customer)
                    .build();

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            return orderItemMapper.mapOrderItemToOrderItemResponse(savedOrderItem);

        } else {
            HttpSession session = httpServletRequest.getSession();
            cart = cartService.getCartBySession(session);

            // Sipariş öğesini oluşturun ve kaydedin (anonim kullanıcı için müşteri olmadan)
            OrderItem orderItem = OrderItem.builder()
                    .quantity(orderItemRequest.getQuantity())
                    .product(product)
                    .totalPrice(orderItemRequest.getQuantity() * product.getPrice())
                    .cart(cart)
                    .build();

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            return orderItemMapper.mapOrderItemToOrderItemResponse(savedOrderItem);
        }
    }

    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemRepository.findAll().stream().map(orderItemMapper::mapOrderItemToOrderItemResponse).collect(Collectors.toList());
    }

    public OrderItemResponse getOrderById(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

        return orderItemMapper.mapOrderItemToOrderItemResponse(orderItem);

    }


    public OrderItemResponse updateOrderItem(OrderItemRequestForUpdate orderItemRequestForUpdate, HttpServletRequest httpServletRequest, Long orderItemId) {
        String username = (String) httpServletRequest.getAttribute("username");

        Cart cart;  //TODO:cartı da controllerda cartid olarak almayı düşün

        // Ürünü alın
        Product product = productService.isProductExistsById(orderItemRequestForUpdate.getProductId());

        // Stok kontrolü
        if (product.getStock() < orderItemRequestForUpdate.getQuantity()) {
            throw new ResourceNotFoundException("Insufficient stock for product: " + product.getProductName());
        }

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() ->
                new ResourceNotFoundException
                        (String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND_MESSAGE, orderItemId)));

        if (username != null) {
            User customer = userService.getUserByUserNameReturnsUser(username);
            cart = cartService.getCartByUsername(username);

            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequestForUpdate.getQuantity());

            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
            return orderItemMapper.mapOrderItemToOrderItemResponse(updatedOrderItem);

        } else {
            HttpSession session = httpServletRequest.getSession();
            cart = cartService.getCartBySession(session);

            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequestForUpdate.getQuantity());
            // Sipariş öğesini oluşturun ve kaydedin (anonim kullanıcı için müşteri olmadan)

            OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
            return orderItemMapper.mapOrderItemToOrderItemResponse(updatedOrderItem);
        }

    }
}
