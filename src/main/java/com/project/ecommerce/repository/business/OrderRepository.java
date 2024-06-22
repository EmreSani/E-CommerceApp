package com.project.ecommerce.repository.business;

import com.project.ecommerce.entity.concretes.business.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order , Long> {
    @Query(value = "SELECT o FROM Order o WHERE o.customer.id = :userId")
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.username = :username")
    Page<Order> findByUsername(String username, Pageable pageable);
}
