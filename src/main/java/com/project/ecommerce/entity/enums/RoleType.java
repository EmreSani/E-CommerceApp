package com.project.ecommerce.entity.enums;

public enum RoleType {

    ADMIN("Admin"),
    CUSTOMER("Customer");

    public final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
