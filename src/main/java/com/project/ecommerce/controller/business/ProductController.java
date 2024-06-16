package com.project.ecommerce.controller.business;

import com.project.ecommerce.payload.request.business.ProductRequest;
import com.project.ecommerce.payload.response.business.ProductResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.service.business.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    //1-product ekleme ->http://localhost:8080/products/add
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<ProductResponse> addProduct (@RequestBody @Valid ProductRequest productRequest){
        return productService.addProduct(productRequest);
    }

    //2-Tüm productları getirme ->http://localhost:8080/products
    @GetMapping
    public ResponseMessage<List<ProductResponse>> getAllProducts (){
        return productService.getAllProducts();
    }

    //3-Id ile product getirme ->http://localhost:8080/products/5
    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseMessage<ProductResponse> getProductById (@PathVariable Long productId){
        return productService.getProductById(productId);
    }

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

}
