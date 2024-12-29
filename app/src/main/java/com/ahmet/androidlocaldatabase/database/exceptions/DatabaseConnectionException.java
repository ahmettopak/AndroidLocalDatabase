package com.ahmet.androidlocaldatabase.database.exceptions;

/**
 * Author: Ahmet TOPAK
 * Since: 12/29/2024
 */
// Custom Exception for Database Connection Failure
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super(message);
    }
}