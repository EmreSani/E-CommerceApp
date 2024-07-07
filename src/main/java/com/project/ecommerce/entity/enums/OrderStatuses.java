package com.project.ecommerce.entity.enums;

import com.project.ecommerce.exception.InvalidOrderStatusException;

public enum OrderStatuses {
    ORDERED("Ordered"),
    CANCELED("Canceled"),
    PREPARING_TO_SHIPPING("Preparing to shipping"),
    IN_CART("In Cart"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered");

    public final String name;

    OrderStatuses(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static OrderStatuses fromString(String status) {
        for (OrderStatuses orderStatus : OrderStatuses.values()) {
            if (orderStatus.name.equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new InvalidOrderStatusException("Unknown status: " + status);
    }
}
