package com.project.ecommerce.repository.business;

import com.project.ecommerce.entity.concretes.business.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order , Long> {
}
