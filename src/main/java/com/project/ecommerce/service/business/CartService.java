package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.repository.business.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private CartRepository cartRepository;

    public Cart getCartByUsername(String username) {
        return cartRepository.
                findByUsername(username).
                orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.CART_COULDNT_FOUND, username)));
    }

    public void clearCart(Cart cart) {
        cart.getOrderItemList().clear();
        cart.recalculateTotalPrice();
        cartRepository.save(cart);
    }

}

