package com.rentacar.services;

import com.rentacar.entities.Customer;

import java.util.List;

public interface ICustomerService {

    public Customer getByIdCustomer(Long id);

    public Customer saveCustomer(Customer customer);

    public List<Customer> getAllCustomer();

    public Customer deleteByIdCustomer(Long id);

    public Customer updateByIdCustomer(Customer customer);
}
