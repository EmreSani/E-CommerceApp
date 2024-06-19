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
    // POST http://localhost:8080/orderItem/save - Endpoint to create a new order item and add it to the cart based on provided parameters

    @PostMapping("/save")
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody @Valid OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(orderItemService.createOrderItem(orderItemRequest, httpServletRequest));
    }

    //2-tüm sipariş ögelerini getirme ->http://localhost:8080/orders
    // GET http://localhost:8080/orderItem - Endpoint to retrieve all order items (requires ADMIN authority)

    //TODO: pageable döndürsün
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    //3-Id ile sipariş ögesi getirme ->http://localhost:8080/orders/5
    // GET http://localhost:8080/orderItem/{orderItemId} - Endpoint to retrieve an order item by its ID (requires ADMIN authority)

    @GetMapping("/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderItemResponse> getOrderById(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderItemService.getOrderById(orderItemId));
    }

    //4-Id ile sipariş ögesi miktarını update etme, carttan silme veya carttaki sayısını arttırıp azaltma ->http://localhost:8080/orderItem/update/5 //quantity=0 ise siparişi sil //Burayı cartta mı düzenlemek lazım acaba
    // PUT http://localhost:8080/orderItem/update/{orderItemId} - Endpoint to update an order item's quantity or delete it from the cart based on provided parameters

    @PutMapping("/update/{orderItemId}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@RequestBody @Valid OrderItemRequestForUpdate orderItemRequestForUpdate,
                                                             @PathVariable Long orderItemId,
                                                             HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(orderItemService.updateOrDeleteOrderItem(orderItemRequestForUpdate, httpServletRequest, orderItemId));
    }

// DELETE http://localhost:8080/orderItem/delete/{orderItemId} - Endpoint to delete an order item by its ID (requires ADMIN authority)

    //order item silinince
    @DeleteMapping("/delete/{orderItemId}")
    public ResponseEntity<OrderItemResponse> deleteOrderItem(@RequestBody @Valid OrderItemRequestForUpdate orderItemRequestForUpdate,
                                                             @PathVariable Long orderItemId,
                                                             HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(orderItemService.deleteOrderItem(orderItemRequestForUpdate, orderItemId, httpServletRequest));
    }

//

    // GET http://localhost:8080/orderItem/page?page=1 &size=&sort=id&direction=ASC - Endpoint to retrieve all order items paginated and sorted by specified parameters
    @GetMapping("/page")
    public ResponseMessage<Page<OrderItemResponse>> getAllOrderItemsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return orderItemService.getAllOrderItemsByPage(page, size, sort, type);
    }

    //10-Idsi verilen müşterinin tüm siparişlerini getirme -> http://localhost:8080/customers/allorder/1 //düzelt
    // GET http://localhost:8080/orderItem/allorder/{userId} - Endpoint to retrieve all order items of a user by user ID (requires ADMIN authority)
    @GetMapping("/allorder/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<List<OrderItemResponse>> getUsersOrderItemsById(
            @PathVariable Long id
    ) {
        return orderItemService.getUsersOrderItemsById(id);

    }

}
