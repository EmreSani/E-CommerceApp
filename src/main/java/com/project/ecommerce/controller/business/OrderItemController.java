package com.project.ecommerce.controller.business;

import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.service.business.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/orderItemList")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping("/save")
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody @Valid OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItemRequest, httpServletRequest));
    }


//    1-sipariş oluşturma ->http://localhost:8080/orders/save/filter?cid=1&prod=1&quant=3 //farklı üründe yeni sipariş, aynı üründe sayı artırılır
//            2-tüm siparişleri getirme ->http://localhost:8080/orders
//            3-Id ile sipariş getirme ->http://localhost:8080/orders/5
//            4-Id ile sipariş miktarını update etme ->http://localhost:8080/orders/update/5/quantity/30 //quantity=0 ise siparişi sil
//            5-Id ile sipariş delete etme ->http://localhost:8080/orders/delete?id=5
//    ÖDEV:-tüm siparişleri page page gösterme-> http://localhost:8080/orders/page?page=1 &size=&sort=id&direction=ASC



}
