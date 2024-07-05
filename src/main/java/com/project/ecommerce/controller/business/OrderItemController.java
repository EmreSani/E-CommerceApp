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

    // 1 - Endpoint to create a new order item and add it to the cart based on provided parameters
    // POST http://localhost:8080/orderItem/save
    @PostMapping("/save")
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody @Valid OrderItemRequest orderItemRequest, HttpServletRequest httpServletRequest) {
        return orderItemService.createOrderItem(orderItemRequest, httpServletRequest);
    }

    // 2 - Endpoint to retrieve all order items (requires ADMIN authority)
    // GET http://localhost:8080/orderItem
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    // 3 - Endpoint to retrieve an order item by its ID (requires ADMIN authority)
    // GET http://localhost:8080/orderItem/{orderItemId}
    @GetMapping("/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderItemResponse> getOrderById(@PathVariable Long orderItemId) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(orderItemId));
    }

    // 4 - Endpoint to update an order item's quantity based on provided parameters
    // PUT http://localhost:8080/orderItem/update/{orderItemId}
    @PutMapping("/update/{orderItemId}")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@RequestBody @Valid OrderItemRequestForUpdate orderItemRequestForUpdate,
                                                             @PathVariable Long orderItemId,
                                                             HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(orderItemRequestForUpdate, httpServletRequest, orderItemId));
    }

    // 5 - Endpoint to delete an order item by its ID
    // DELETE http://localhost:8080/orderItem/delete/{orderItemId}
    // Order item is removed from the cart when deleted
    @DeleteMapping("/delete/{orderItemId}")
    public ResponseEntity<OrderItemResponse> deleteOrderItemById(@PathVariable Long orderItemId,
                                                                 HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(orderItemService.deleteOrderItemById(orderItemId, httpServletRequest));
    }

//

    // 6 - Endpoint to retrieve all order items paginated and sorted by specified parameters (requires ADMIN authority)
    // GET http://localhost:8080/orderItem/page?page=1&size=&sort=id&direction=ASC
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<Page<OrderItemResponse>> getAllOrderItemsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return orderItemService.getAllOrderItemsByPage(page, size, sort, type);
    }

//    //10-Idsi verilen müşterinin tüm siparişleri ögelerini getirme -> http://localhost:8080/customers/allorder/1 //order itemları userlarla değil cartlarla eşleştirdiğim için şuan inaktif
//    // GET http://localhost:8080/orderItem/allorder/{userId} - Endpoint to retrieve all order items of a user by user ID (requires ADMIN authority)
//    @GetMapping("/allorder/{userId}")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    public ResponseMessage<List<OrderItemResponse>> getUsersOrderItemsById(
//            @PathVariable Long userId
//    ) {
//        return orderItemService.getUsersOrderItemsById(userId);
//
//    }

}
