package com.miage.ebankbackend.exceptions;

public class CustomerNotFoundException extends Exception{
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
