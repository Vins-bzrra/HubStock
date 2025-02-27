package com.vins.hubstock.entity;



public enum UserRole {
    ADMIN("ADMIN"),
    OPERATOR("OPERATOR"),
    USER("USER");

    private final String userRole;

    UserRole(String userRole){
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return userRole;
    }
}
