package com.project.ecommerce.controller.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.service.business.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    //TODO: Userın yapabildiği "/orders/cancel/**",
    //            "/orderItem/save",
    //            "/orderItem/update/**", //check this maybe
    //            "/orderItem/delete/**",
    // ve orderCreate methoduna anonimlerin de ulaşabileceği şeklide dizayn et.
    // 23.06.2024 itibariyle sadece sessiondan cart oluşturabiliyor anonim kullanıcı, siparişle alakalı konularda.
    @GetMapping // GET http://localhost:8080/cart?username=user2 - Endpoint to retrieve cart details
    public Cart getCart(@RequestParam(required = false) String username, HttpSession session) {
        if (username != null) {
            return cartService.getCartByUsername(username);
        } else {
            return cartService.getCartBySession(session);
        }
    }

    //updateCart ortemItem methodları içerisinde sağlandı.
}
