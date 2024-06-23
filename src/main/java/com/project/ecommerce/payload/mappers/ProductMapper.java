package com.project.ecommerce.payload.mappers;

import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.payload.request.business.ProductRequest;
import com.project.ecommerce.payload.response.business.ProductResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProductMapper {

    public Product mapProductRequestToProduct(ProductRequest productRequest) {
        return Product.builder().productName(productRequest.getProductName())
                .brand(productRequest.getBrand())
                .price(productRequest.getPrice())
                .stock(productRequest.getQuantity())
                .build();
    }

    public ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder().id(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .brand(product.getBrand())
                .stock(product.getStock())//stock bilgisi eklendi
                .build();
    }

    public Product mapProductRequestToUpdatedProduct(ProductRequest productRequest, Long id) {
        return Product.builder().id(id)
                .productName(productRequest.getProductName())
                .brand(productRequest.getBrand())
                .price(productRequest.getPrice())
                .stock(productRequest.getQuantity())
                .build();
    }

}
