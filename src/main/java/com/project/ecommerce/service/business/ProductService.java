package com.project.ecommerce.service.business;

import com.project.ecommerce.entity.concretes.business.OrderItem;
import com.project.ecommerce.entity.concretes.business.Product;
import com.project.ecommerce.entity.concretes.user.User;
import com.project.ecommerce.exception.ConflictException;
import com.project.ecommerce.exception.ResourceNotFoundException;
import com.project.ecommerce.payload.mappers.ProductMapper;
import com.project.ecommerce.payload.messages.ErrorMessages;
import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.business.ProductRequest;
import com.project.ecommerce.payload.response.business.ProductResponse;
import com.project.ecommerce.payload.response.business.ResponseMessage;
import com.project.ecommerce.payload.response.user.UserResponse;
import com.project.ecommerce.repository.business.ProductRepository;
import com.project.ecommerce.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PageableHelper pageableHelper;

    public void updateProductStockForCancellingOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    public void updateProductStockForCreatingOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        product.setStock(product.getStock() - quantity);
        if (product.getStock() < 0) {
            throw new ResourceNotFoundException(ErrorMessages.NOT_ENOUGH_STOCK_PRODUCT_MESSAGE);
        }
        productRepository.save(product);
    }

    public ResponseMessage<ProductResponse> addProduct(ProductRequest productRequest) {

        checkProductNameDuplicate(productRequest.getProductName());

        Product product = saveProductUsingMapper(productRequest);

        return ResponseMessage.<ProductResponse>builder()
                .message(String.format(SuccessMessages.PRODUCT_CREATE, product.getId()))
                .httpStatus(HttpStatus.OK)
                .object(productMapper.mapProductToProductResponse(product))
                .build();
    }

    private Product saveProductUsingMapper(ProductRequest productRequest) {
        Product product = productMapper.mapProductRequestToProduct(productRequest);
        return productRepository.save(product);
    }

    public void checkProductNameDuplicate(String productName) {
        if (productRepository.existsByProductName(productName)) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PRODUCTNAME, productName));
        }
    }

    public ResponseMessage<List<ProductResponse>> getAllProducts() {
        return ResponseMessage.<List<ProductResponse>>builder()
                .message(SuccessMessages.PRODUCTS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(productRepository.
                        findAll().
                        stream().
                        map(productMapper::mapProductToProductResponse).
                        collect(Collectors.toList()))
                .build();
    }

    public ResponseMessage<ProductResponse> getProductById(Long productId) {
        return ResponseMessage.<ProductResponse>builder()
                .message(SuccessMessages.PRODUCT_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(productMapper.mapProductToProductResponse(isProductExistsById(productId)))
                .build();
    }

    public Product isProductExistsById(Long id) {
        return productRepository.
                findById(id).
                orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_PRODUCT_MESSAGE, id)));
    }

    public ResponseMessage<Page<ProductResponse>> getAllProductsByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        return ResponseMessage.<Page<ProductResponse>>builder()
                .message(SuccessMessages.PRODUCTS_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(productRepository.findAll(pageable).map(productMapper::mapProductToProductResponse))
                .build();
    }


    public ResponseMessage<ProductResponse> updateProduct(Long productId, ProductRequest productRequest) {
        Product existingProduct = isProductExistsById(productId);

        if (!existingProduct.getProductName().equals(productRequest.getProductName())) {
            checkProductNameDuplicate(productRequest.getProductName());
        }

        Product updatedProduct = productMapper.mapProductRequestToUpdatedProduct(productRequest, productId);

        updatedProduct = productRepository.save(updatedProduct);

        return ResponseMessage.<ProductResponse>builder()
                .message(SuccessMessages.PRODUCT_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(productMapper.mapProductToProductResponse(updatedProduct))
                .build();
    }

    public List<Product> findAllProductsForMainClass() {

        return productRepository.findAll();
    }

    @Transactional
    public ResponseMessage<List<ProductResponse>> getProductsByNameContainsTheseLetters(String letters) {

        List<Product> productList = productRepository.findByNameContains(letters);

            if (productList.isEmpty()) {
                throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT_MESSAGE_CONTAINS_LETTERS);
            }

            return ResponseMessage.<List<ProductResponse>>builder().
                    message(SuccessMessages.USERS_FOUND).
                    httpStatus(HttpStatus.OK).
                    object(productList.stream().
                            map(productMapper::mapProductToProductResponse).
                            collect(Collectors.toList())).
                    build();
        }


//    public void deleteProductById(Long productId) {
//
//        Product productToDelete = isProductExistsById(productId);
//
//        List<OrderItem> orderItems = productToDelete.getOrderItemList();
//
//        for (OrderItem orderItem : orderItems) {
//
//            if (orderItem.getOrderItemStatus().equalsIgnoreCase("something")) {
//
//            }
//
//        }
//        if (productToDelete.getOrderItemList() != null && !productToDelete.getOrderItemList().isEmpty()) {
//            throw new ConflictException(ErrorMessages.PRODUCT_CAN_NOT_BE_DELETED);
//        }
//
//        productRepository.delete(productToDelete);
//    }
}
