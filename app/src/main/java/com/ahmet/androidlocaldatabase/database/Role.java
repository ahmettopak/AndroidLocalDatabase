package com.ahmet.androidlocaldatabase.database;

/**
 * Author: Ahmet TOPAK
 * Since: 12/29/2024
 */

public enum Role {
    SUPER_USER, ADMIN, USER;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER; // Default role
        }
    }
}
