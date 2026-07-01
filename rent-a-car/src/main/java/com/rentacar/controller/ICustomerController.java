package com.rentacar.controller;

import com.rentacar.entities.Customer;

public interface ICustomerController {

    public Customer getByIdCustomer(Long id);

    public Customer saveCustomer(Customer customer);
}
