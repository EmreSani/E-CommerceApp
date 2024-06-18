package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.Order;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.OrderItemMapper;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.repository.business.OrderItemRepository;
import com.project.ecommerce.repository.business.OrderRepository;
import com.project.ecommerce.service.helper.MethodHelper;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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

    public OrderItem createOrderItem(OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {

        String username = (String) httpServletRequest.getAttribute("username");
        Cart cart;

        if (username != null) {
            User customer = userService.getUserByUserNameReturnsUser(username);
            Product product = productService.isProductExistsById(orderItemRequest.getProductId());

            // Stok kontrolü
            if (product.getStock() < orderItemRequest.getQuantity()) {
                throw new ResourceNotFoundException("Insufficient stock for product: " + product.getName());
            }

            cart = cartService.getCartByUsername(username);
        } else {
            HttpSession session = httpServletRequest.getSession();
            cart = cartService.getCartBySession(session);
        }
//       String username = (String) httpServletRequest.getAttribute("username");
//
//       User customer = userService.getUserByUserNameReturnsUser(username);
//
//        Product product = productService.isProductExistsById(orderItemRequest.getProductId());
//
//        Cart cart = cartService.getCartByUsername(username);
//
//        Double totalPrice = orderItemRequest.getQuantity() * product.getPrice();

        OrderItem orderItem = OrderItem.builder()
                .quantity(orderItemRequest.getQuantity())
                .product(product)
                .totalPrice(totalPrice)
                .cart(cart)
                .customer(customer)
                .build();

        return orderItemRepository.save(orderItem);


    }
}
