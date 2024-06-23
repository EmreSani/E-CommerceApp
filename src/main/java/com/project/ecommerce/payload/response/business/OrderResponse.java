package com.project.ecommerce.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.ecommerce.entity.concretes.business.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private Long id;
    private Long customerId;  // customer yerine customerId kullanabilirsiniz
    private List<OrderItemResponse> orderItems = new ArrayList<>();
    private Long cartId;  // cart yerine cartId kullanabilirsiniz
    private LocalDateTime orderDate;
    private String orderStatus;
    private Double totalPrice;
    //orderStatus added
}
