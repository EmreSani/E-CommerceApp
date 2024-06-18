package com.project.ecommerce.payload.request.business;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Please enter the product name")
    @Size(min = 4, max = 16,message = "Your username should be at least 4 chars")
    private String productName;

    @NotNull(message = "Please enter the brand of the product")
    @Size(min = 4, max = 16,message = "Your username should be at least 4 chars")
    private String brand;

    @NotNull(message = "Please enter the price of the product")
    private Double price;

}
