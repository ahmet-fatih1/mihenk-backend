package com.rentacar.repository;

import com.rentacar.entities.Customer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
