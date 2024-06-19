package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.payload.response.business.OrderResponse;
import com.project.ecommerce.payload.response.business.ProductResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<OrderResponse> createOrder(@AuthenticationPrincipal Principal principal) {

        String username = principal.getName();

        return orderService.createOrderFromCart(username);

    }

   // 5-Id ile order delete etme ->http://localhost:8080/orders/delete?id=5
    //order silinince içerisindeki order itemlar da silinir
    @DeleteMapping("/delete/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderResponse> deleteOrderItem(@PathVariable Long orderId){

        return ResponseEntity.ok(orderService.deleteOrderById(orderId));

    }

    // 6-tüm siparişleri page page gösterme-> http://localhost:8080/orders/page?page=1 &size=&sort=id&direction=ASC
    @GetMapping("/page")
    public ResponseMessage<Page<OrderResponse>> getAllOrdersByPage (
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return orderService.getAllOrdersByPage(page,size,sort,type);
    }
}

