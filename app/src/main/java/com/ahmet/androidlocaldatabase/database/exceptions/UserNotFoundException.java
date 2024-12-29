package com.ahmet.androidlocaldatabase.database.exceptions;

/**
 * Author: Ahmet TOPAK
 * Since: 12/29/2024
 */
// Custom Exception for User Not Found
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
