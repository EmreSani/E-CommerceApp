package com.project.ecommerce.payload.mappers;

import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.payload.request.business.ProductRequest;
import com.project.ecommerce.payload.response.business.ProductResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProductMapper {

    public Product mapProductRequestToProduct(ProductRequest productRequest) {
        return Product.builder().productName(productRequest.getProductName())
                .brand(productRequest.getBrand())
                .price(productRequest.getPrice())
                .build();
    }

    public ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder().productName(product.getProductName())
                .price(product.getPrice())
                .brand(product.getBrand())
                .id(product.getId())
                .build();
    }

}
