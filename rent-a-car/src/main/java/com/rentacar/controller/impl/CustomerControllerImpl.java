package com.rentacar.controller.impl;

import com.rentacar.controller.ICustomerController;
import com.rentacar.entities.Customer;
import com.rentacar.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(path = "/list")
    @Override
    public List<Customer> getAllCustomer() {
        return customerService.getAllCustomer();
    }

    @DeleteMapping(path = "/delete/{id}")
    @Override
    public Customer deleteCustomer(@PathVariable(name = "id") Long id) {
        return customerService.deleteByIdCustomer(id);
    }


    @PutMapping(path = "/{id}")
    @Override
    public Customer updateByIdCustomer(@PathVariable(name = "id") Long id, @RequestBody Customer customer) {

        return customerService.updateByIdCustomer(customer);
    }


    @PostMapping(path = "/save")
    @Override
    public Customer saveCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }





}
