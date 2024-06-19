package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.request.business.OrderItemRequest;
import com.project.ecommerce.payload.request.business.OrderItemRequestForUpdate;
import com.project.ecommerce.payload.response.business.OrderItemResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orderItem")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    //1-sipariş ögesi oluşturma ->http://localhost:8080/orders/save/filter?cid=1&prod=1&quant=3 //bir productı istediğimiz sayıda alıp carta ekliyoruz.

    @PostMapping("/save")
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody @Valid OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItemRequest, httpServletRequest));
    }

    //2-tüm sipariş ögelerini getirme ->http://localhost:8080/orders
    //TODO: pageable döndürsün
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    //3-Id ile sipariş ögesi getirme ->http://localhost:8080/orders/5
    @GetMapping("/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderItemResponse> getOrderById(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderItemService.getOrderById(orderItemId));
    }

    //4-Id ile sipariş ögesi miktarını update etme, carttan silme veya carttaki sayısını arttırıp azaltma ->http://localhost:8080/orders/update/5 //quantity=0 ise siparişi sil //Burayı cartta mı düzenlemek lazım acaba
    @PutMapping("/update/{orderItemId}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@RequestBody @Valid OrderItemRequestForUpdate orderItemRequestForUpdate,
                                                             @PathVariable Long orderItemId,
                                                             HttpServletRequest httpServletRequest
                                                             ) {
        return ResponseEntity.ok(orderItemService.updateOrDeleteOrderItem(orderItemRequestForUpdate, httpServletRequest, orderItemId));
    }


// 5-Id ile sipariş ögesi delete etme ->http://localhost:8080/orders/delete?id=5
    //order item silinince
    @DeleteMapping("/delete/{orderItemId}")
    public ResponseEntity<OrderItemResponse> deleteOrderItem (@RequestBody @Valid OrderItemRequestForUpdate orderItemRequestForUpdate,
                                                              @PathVariable Long orderItemId,
                                                              HttpServletRequest httpServletRequest
    ){
        return ResponseEntity.ok(orderItemService.deleteOrderItem(orderItemRequestForUpdate,orderItemId,httpServletRequest));
    }

//


//    6-tüm sipariş ögelerini page page gösterme-> http://localhost:8080/orders/page?page=1 &size=&sort=id&direction=ASC
@GetMapping("/page")
public ResponseMessage<Page<OrderItemResponse>> getAllOrderItemsByPage (
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort", defaultValue = "name") String sort,
        @RequestParam(value = "type", defaultValue = "desc") String type
){
    return orderItemService.getAllOrderItemsByPage(page,size,sort,type);
}

}
