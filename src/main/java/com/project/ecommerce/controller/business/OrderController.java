package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.response.business.OrderResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
//1- Order oluşturma. Binevi fiş.
// POST http://localhost:8080/orders/create - Endpoint to create a new order from the authenticated user's cart
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<OrderResponse> createOrder(HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        String username = principal.getName();

        return orderService.createOrderFromCart(username);
    }

    // 2-Id ile order delete etme ->http://localhost:8080/orders/delete/5
    //order silinince içerisindeki order itemlar da silinir
    // DELETE http://localhost:8080/orders/delete/{orderId} - Endpoint to delete an order by its ID, including its order items
    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderId) {
        orderService.deleteOrderById(orderId);
        return ResponseEntity.noContent().build();
    }

    // 3-tüm siparişleri page page gösterme-> http://localhost:8080/orders/page?page=1 &size=&sort=id&direction=ASC
    // GET http://localhost:8080/orders/page - Endpoint to retrieve all orders paginated and sorted by specified parameters

    @GetMapping("/page")
    public ResponseMessage<Page<OrderResponse>> getAllOrdersByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return orderService.getAllOrdersByPage(page, size, sort, type);
    }


}

