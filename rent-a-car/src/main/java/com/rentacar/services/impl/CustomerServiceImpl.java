package com.rentacar.services.impl;

import com.rentacar.entities.Customer;
import com.rentacar.repository.CustomerRepository;
import com.rentacar.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public Customer getByIdCustomer(Long id) {

        Optional<Customer> optional = customerRepository.findById(id);

        if(optional.isPresent()){

            Customer foundcustomer = optional.get();

            return foundcustomer;
        }
        return null;
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        Customer dbcustomer = customerRepository.save(customer);

        return dbcustomer;


    }


}
