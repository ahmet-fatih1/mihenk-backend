package com.rentacar.common.exception;

import com.rentacar.Customer.Customer;

public class CustomerAlreadyExistsException extends RuntimeException{
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
