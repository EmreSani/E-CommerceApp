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
}
