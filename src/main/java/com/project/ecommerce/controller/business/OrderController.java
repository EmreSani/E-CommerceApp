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

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
//1- Order oluşturma. Binevi fiş.
// POST http://localhost:8080/orders/create - Endpoint to create a new order from the authenticated or anonymous user's cart
//    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<OrderResponse> createOrder(HttpServletRequest HttpServletrequest) {

        return orderService.createOrderFromCart(HttpServletrequest);

    }

    // 2-Id ile order delete etme ->http://localhost:8080/orders/delete/5
    //sadece admin silebilir, ve sadece daha öncesinde cancel edilen orderları silebiliyor, geriye kalan orderları dbde record olarak tutma amacındayız.
    //order silinince içerisindeki order itemlar da silinir, sipariş daha öncesinde zaten iptal edildiği için stock güncellenmez.
    // DELETE http://localhost:8080/orders/delete/{orderId} - Endpoint to delete an order by its ID, including its order items
    @DeleteMapping("/delete/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderResponse> deleteOrderItem(@PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.deleteOrderById(orderId));
    }

    // 3-tüm siparişleri page page gösterme-> http://localhost:8080/orders/page?page=1 &size=&sort=id&direction=ASC
    // GET http://localhost:8080/orders/page - Endpoint to retrieve all orders paginated and sorted by specified parameters
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


    //Idsi verilmiş kullanıcının tüm siparişlerini getir
    // 4-http://localhost:8080/orders/page?page=1&size=&sort=id&direction=ASC
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

    // cancel order, updates the product stock.
    @PostMapping("/cancel/{orderId}")// http://localhost:8080/orders/cancel/{orderId}
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId, HttpServletRequest httpServletRequest) {

        OrderResponse orderResponse = orderService.cancelOrderById(orderId, httpServletRequest);

        return ResponseEntity.ok(orderResponse);
    }

    //todo: write a cancelation method that anonymous users can also cancel their order.

    // Implement method to get all orders of the logged-in user
    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<Page<OrderResponse>> getMyOrdersByPage(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        //we get the username from the request and fetch orders for the logged-in user
        String username = request.getUserPrincipal().getName();
        return orderService.getOrdersByUsername(username, page, size, sort, type);
    }


}

