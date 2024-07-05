package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.business.ProductRequest;
import com.project.ecommerce.payload.response.business.ProductResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // POST http://localhost:8080/products/add - Endpoint to add a new product (requires ADMIN authority)
    //1-product ekleme ->http://localhost:8080/products/add
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<ProductResponse> addProduct (@RequestBody @Valid ProductRequest productRequest){
        return productService.addProduct(productRequest);
    }

    // updateProduct
    // PUT http://localhost:8080/products/update/{productId} - Endpoint to update an existing product (requires ADMIN authority)
    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductRequest productRequest) {
        return productService.updateProduct(productId, productRequest);
    }

    // GET http://localhost:8080/products - Endpoint to retrieve all products
    //2-Tüm productları getirme ->http://localhost:8080/products
    @GetMapping
    public ResponseMessage<List<ProductResponse>> getAllProducts (){
        return productService.getAllProducts();
    }

    // GET http://localhost:8080/products/{productId} - Endpoint to retrieve a product by its ID (requires ADMIN or CUSTOMER authority)
    //3-Id ile product getirme ->http://localhost:8080/products/5
    @GetMapping("/{productId}")
 //   @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')") anonimler de yapabilsin.
    public ResponseMessage<ProductResponse> getProductById (@PathVariable Long productId){
        return productService.getProductById(productId);
    }

    // GET http://localhost:8080/products/page - Endpoint to retrieve all products paginated and sorted by specified parameters
    //4-tüm productları page page gösterme ->http://localhost:8080/products/page?page=1&size=2 &sort=id&direction=ASC
    @GetMapping("/page")
    public ResponseMessage<Page<ProductResponse>> gelAllProductsByPage (
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return productService.getAllProductsByPage(page,size,sort,type);
    }

//    //product silme methodunu değerlendir; orderitemların ve orderların durumunu gözet.
//    @DeleteMapping("/delete/{productId}")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    public ResponseMessage deleteProductById(@PathVariable Long productId){
//
//        productService.deleteProductById(productId);
//
//      return ResponseMessage.builder().message(SuccessMessages.PRODUCT_DELETE)
//                .httpStatus(HttpStatus.NO_CONTENT)
//                .build();
//
//    }
}
