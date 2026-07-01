package com.rentacar.controller.impl;

import com.rentacar.controller.ICustomerController;
import com.rentacar.entities.Customer;
import com.rentacar.services.ICustomerService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/api/customer")
public class CustomerControllerImpl implements ICustomerController {


    @Autowired
    private ICustomerService customerService;

    @GetMapping(path = "/list/{id}")
    @Override
    public Customer getByIdCustomer(@PathVariable(name = "id") Long id) {

        return customerService.getByIdCustomer(id);

    }


    @PostMapping(path = "/save")
    @Override
    public Customer saveCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }


}
