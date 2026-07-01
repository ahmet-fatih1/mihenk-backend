package com.rentacar.services;

import com.rentacar.entities.Customer;

public interface ICustomerService {

    public Customer getByIdCustomer(Long id);

    public Customer saveCustomer(Customer customer);

}
