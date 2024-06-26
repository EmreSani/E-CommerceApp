package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.repository.business.CartRepository;
import com.project.ecommerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public Cart getCartByUsername(String username) {
        logger.info("Fetching cart for username: {}", username);
        return cartRepository.
                findByUserUsername(username).
                orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.CART_COULDNT_FOUND, username)));
    }

    @Transactional
    public void clearCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        logger.info("Clearing cart with id: {}", cart.getId());
        cart.getOrderItemList().clear();
        cart.recalculateTotalPrice();
        cartRepository.save(cart);
    }

    public Cart getCartBySession(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        String sessionId = session.getId();
        return cartRepository.findBySessionId(sessionId).orElseGet(() -> createCartForSession(session));
    }

    private Cart createCartForSession(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        Cart cart = new Cart();
        cart.setOrderItemList(new ArrayList<>());
        cart.setSessionId(session.getId());
        cart.setTotalPrice(0.0);
        return cartRepository.save(cart);
    }

    public Cart createCartForUser(User user){
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    public void saveCart (Cart cart){
        cartRepository.save(cart);
    }

}

