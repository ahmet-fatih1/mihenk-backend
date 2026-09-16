package com.rentacar.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByNationalId(String nationalId);
    boolean existsByEmail(String email);
}
