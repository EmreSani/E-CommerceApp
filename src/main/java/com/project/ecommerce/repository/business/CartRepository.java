package com.project.ecommerce.repository.business;

import com.project.ecommerce.entity.concretes.business.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {


    Optional<Cart> findByUserUsername(String username);
    Optional<Cart> findBySessionId(String sessionId);
}
