package com.project.ecommerce.payload.messages;

public class SuccessMessages {
    private SuccessMessages() {
    }

    public static final String PASSWORD_CHANGED_RESPONSE_MESSAGE = "Password Successfully Changed" ;
    public static final String USER_CREATE = "User saved successfully with id: %s";

    public static final String USER_DELETE = "User is deleted successfully with id: %s";
    public static final String USER_FOUND = "User is Found Successfully";
    public static final String USERS_FOUND = "Users are Found Successfully";
    public static final String USER_UPDATE = "your information has been updated successfully";
    public static final String USER_UPDATE_MESSAGE = "User is Updated Successfully";

    public static final String ORDER_ITEMS_FOUND = "Order Items Found Successfully";
    public static final String ORDER_ITEM_FOUND = "Order Item Found Successfully";
}
