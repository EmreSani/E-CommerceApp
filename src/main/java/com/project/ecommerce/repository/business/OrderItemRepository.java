package com.project.ecommerce.repository.business;

import com.project.ecommerce.entity.concretes.business.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    @Query(value = "SELECT o FROM OrderItem o WHERE o.user.id = :userId")
    List<OrderItem> findAllByUserId(Long userId);

}
