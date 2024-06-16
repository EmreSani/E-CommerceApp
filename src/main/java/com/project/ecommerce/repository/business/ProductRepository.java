package com.project.ecommerce.repository.business;

import com.project.ecommerce.entity.concretes.business.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

    boolean existsByProductName(String productName);
}
