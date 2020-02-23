package com.ecommerce.ecommApp.customers.exceptions;

public class EmailExistsException extends Exception{
    public EmailExistsException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
