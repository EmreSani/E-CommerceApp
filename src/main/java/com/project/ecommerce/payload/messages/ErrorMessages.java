package com.project.ecommerce.payload.messages;

public class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String NOT_PERMITTED_METHOD_MESSAGE = "You do not have any permission to do this operation";
    public static final String PASSWORD_NOT_MATCHED = "Your passwords are not matched" ;
    public static final String ALREADY_REGISTER_MESSAGE_USERNAME = "Error: User with username %s already registered" ;
    public static final String ALREADY_REGISTER_MESSAGE_SSN = "Error: User with ssn %s already registered" ;
    public static final String ALREADY_REGISTER_MESSAGE_EMAIL = "Error: User with email %s already registered" ;
    public static final String ALREADY_REGISTER_MESSAGE_PHONE = "Error: User with phone number %s already registered";
    public static final String ALREADY_REGISTER_MESSAGE_PRODUCTNAME = "Error: Product with name %s already registered" ;

    public static final String ROLE_NOT_FOUND = "There is no role like that , check the database" ;

    public static final String NOT_FOUND_USER_MESSAGE_WITHOUT_ID = "Error: User not found";
    public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found with id %s";
    public static final String NOT_FOUND_USER_MESSAGE_WITH_USERNAME = "Error: User not found with username %s";

    public static final String NOT_FOUND_PRODUCT_MESSAGE = "Error: Product not found with id %s";
    public static final String NOT_FOUND_PRODUCT_MESSAGE_CONTAINS_LETTERS = "Error: Product not found based on your search";
    public static final String NOT_ENOUGH_STOCK_PRODUCT_MESSAGE = "Error: Unfortunately we do not have enough stock for you order. To further information please contact with manager.";
    public static final String PRODUCT_CAN_NOT_BE_DELETED = "This product cannot be deleted due to business rules: You can delete the product if only product is not in a customers cart";

    public static final String CART_IS_EMPTY ="Error: Cart is empty now, please add your products to your cart first.";
    public static final String CART_COULDNT_FOUND ="Error: Cart couldnt found with username: %s.";

    public static final String ORDER_NOT_FOUND_USER_MESSAGE = "Error: Order not found with id %s";
    public static final String ORDER_CAN_NOT_BE_DELETED = "Order cannot be deleted due to business rules: You can delete order if only order status is cancelled";
    public static final String ORDER_CAN_NOT_BE_CANCELED = "Order cannot be canceled due to business rules, please contact with customer support.";

    public static final String ORDER_ITEM_NOT_FOUND_MESSAGE = "Error: Order Item not found with id %s";

    public static final String ORDER_ITEM_CAN_NOT_BE_UPDATED_MESSAGE = "Error: You cannot update the order item using a different product ID.";
}
