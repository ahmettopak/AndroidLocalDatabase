package com.ahmet.androidlocaldatabase.database.exceptions;

/**
 * Author: Ahmet TOPAK
 * Since: 12/29/2024
 */
// Custom Exception for User Already Exists
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}