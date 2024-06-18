package com.project.ecommerce.payload.request.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRequest {

    private Long id;

    private User customer;


    private List<OrderItem> orderItemResponses = new ArrayList<>();

    private Cart cart;  // Sepet ilişkisi

    private LocalDateTime orderDate;
}
