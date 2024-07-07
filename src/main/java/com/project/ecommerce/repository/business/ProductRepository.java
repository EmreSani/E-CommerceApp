package com.project.ecommerce.repository.business;

import com.project.ecommerce.entity.concretes.business.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByProductName(String productName);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:letters%")
    List<Product> findByNameContains(@Param("letters") String letters);

}
