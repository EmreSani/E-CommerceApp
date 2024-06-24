package com.project.ecommerce.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestForUpdate {

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

//    @NotNull(message = "Product ID cannot be null")
//    private Long productId;
    //kaldırıldı çünkü sipariş öğesini güncellerken product değişsin istemiyoruz.
    //sadece sepetindeki bu öğenin sayısını değiştirsin istiyoruz.
    //product değiştirmek için önce orderItem silinir sonra istediği productı barındıran orderItem eklenir.

}
