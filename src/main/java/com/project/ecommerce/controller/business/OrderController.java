package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.response.business.OrderResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

