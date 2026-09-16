package com.rentacar.Customer;

import com.rentacar.Customer.dto.*;
import com.rentacar.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController{

    private final CustomerService customerService;

    @GetMapping
    public PageResponse<CustomerSummaryResponse> search(
            CustomerSearchCriteria criteria,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable)
    {
        return customerService.search(criteria, pageable);
    }

    @GetMapping(path = "/{id}")
    public CustomerDetailResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDetailResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerDetailResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }







}
