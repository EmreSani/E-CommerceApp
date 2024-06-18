package com.project.ecommerce.controller.business;

import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.payload.request.business.OrderItemRequestForUpdate;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.service.business.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orderItem")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    //1-sipariş oluşturma ->http://localhost:8080/orders/save/filter?cid=1&prod=1&quant=3 //bir productı istediğimiz sayıda alıp carta ekliyoruz.

    @PostMapping("/save")
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody @Valid OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItemRequest, httpServletRequest));
    }

    //2-tüm sipariş ögelerini getirme ->http://localhost:8080/orders
    //TODO: pageable döndürsün
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    //3-Id ile sipariş ögesi getirme ->http://localhost:8080/orders/5
    @GetMapping("/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderItemResponse> getOrderById(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderItemService.getOrderById(orderItemId));
    }

    //4-Id ile sipariş miktarını update etme ->http://localhost:8080/orders/update/5 //quantity=0 ise siparişi sil //Burayı cartta mı düzenlemek lazım acaba
    @PutMapping("/update/{orderItemId}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@RequestBody @Valid OrderItemRequestForUpdate orderItemRequestForUpdate,
                                                             @PathVariable Long orderItemId,
                                                             HttpServletRequest httpServletRequest
                                                             ) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(orderItemRequestForUpdate, httpServletRequest, orderItemId));
    }


//
//            5-Id ile sipariş delete etme ->http://localhost:8080/orders/delete?id=5
//    6-tüm siparişleri page page gösterme-> http://localhost:8080/orders/page?page=1 &size=&sort=id&direction=ASC


}
