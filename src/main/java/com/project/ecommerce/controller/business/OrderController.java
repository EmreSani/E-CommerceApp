package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.request.business.OrderRequestForStatus;
import com.project.ecommerce.payload.response.business.OrderResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    // 1. Endpoint to create a new order from the authenticated or anonymous user's cart
    // POST http://localhost:8080/orders/create
    @PostMapping("/create")
    public ResponseMessage<OrderResponse> createOrder(HttpServletRequest HttpServletrequest) {

        return orderService.createOrderFromCart(HttpServletrequest);

    }

    // 2. Endpoint to delete an order by its ID, including its order items
    // DELETE http://localhost:8080/orders/delete/{orderId}
    // Only admins can delete; order items are deleted along with the order
    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.deleteOrderById(orderId));
    }

    // 3. Endpoint to retrieve all orders paginated and sorted by specified parameters
    // GET http://localhost:8080/orders/page?page=1&size=10&sort=name&type=desc
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<Page<OrderResponse>> getAllOrdersByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return orderService.getAllOrdersByPage(page, size, sort, type);
    }


    // 4. Endpoint to retrieve all orders of a user by user ID
    // GET http://localhost:8080/orders/page/{userId}?page=0&size=10&sort=name&type=desc
    // Requires ADMIN or CUSTOMER authority
    @GetMapping("/page/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<Page<OrderResponse>> getAllOrdersByUserIdByPage(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return orderService.getAllOrdersByUserIdByPage(userId, page, size, sort, type);
    }

    // Endpoint to cancel the authenticated user's order by order ID, updating the product stock and setting order status to "cancelled"
    // POST http://localhost:8080/orders/cancel/{orderId}
    // Requires ADMIN or CUSTOMER authority
    @PostMapping("/cancel/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId, HttpServletRequest httpServletRequest) {

        OrderResponse orderResponse = orderService.cancelOrderById(orderId, httpServletRequest);

        return ResponseEntity.ok(orderResponse);
    }

    // 6. Endpoint to cancel an anonymous user's order by its ID
    // POST http://localhost:8080/orders/cancelAnonymous/{orderId}
    // Allows anonymous users to cancel their order
    @PostMapping("/cancelAnonymous/{orderId}")
    public ResponseEntity<OrderResponse> cancelAnonymousOrder(@PathVariable Long orderId, HttpServletRequest httpServletRequest) {

        OrderResponse orderResponse = orderService.cancelAnonymousOrderById(orderId, httpServletRequest);

        return ResponseEntity.ok(orderResponse);
    }

    // 7. Endpoint to retrieve orders of the logged-in user
    // GET http://localhost:8080/orders/my-orders?page=0&size=10&sort=name&type=desc
    // Requires ADMIN or CUSTOMER authority
    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<Page<OrderResponse>> getMyOrdersByPage(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        // We get the username from the request and fetch orders for the logged-in user
        String username = request.getUserPrincipal().getName();
        return orderService.getOrdersByUsername(username, page, size, sort, type);
    }

    // 8. Endpoint to update order status by its ID
    // PUT http://localhost:8080/orders/update/{orderId}
    // Requires ADMIN authority
    @PutMapping("/update/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<OrderResponse> updateOrderStatus(
            @RequestBody OrderRequestForStatus orderRequestForStatus,
            @PathVariable Long orderId
    ) {
        return orderService.updateOrderStatus(orderRequestForStatus, orderId);
    }


}

