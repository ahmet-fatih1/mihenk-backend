package com.rentacar.Customer;

import com.rentacar.Customer.dto.*;
import com.rentacar.Customer.dto.CreateCustomerRequest;
import com.rentacar.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;


public interface CustomerService {

    CustomerDetailResponse getById(Long id);

    PageResponse<CustomerSummaryResponse> search(CustomerSearchCriteria criteria, Pageable pageable);

    CustomerDetailResponse create(CreateCustomerRequest request);

    CustomerDetailResponse update(Long id, UpdateCustomerRequest request);

    void delete(Long id);   // soft delete → void yeter
}

