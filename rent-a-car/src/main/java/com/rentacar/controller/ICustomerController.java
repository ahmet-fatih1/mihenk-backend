package com.rentacar.controller;

import com.rentacar.entities.Customer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ICustomerController {

    public Customer getByIdCustomer(Long id);

    public Customer saveCustomer(Customer customer);

    public List<Customer> getAllCustomer();

    public Customer deleteCustomer(Long id);

    public Customer updateByIdCustomer(Long id,Customer customer);
}
