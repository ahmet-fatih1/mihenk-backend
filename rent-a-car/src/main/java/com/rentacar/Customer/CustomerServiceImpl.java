package com.rentacar.Customer;

import com.rentacar.Customer.dto.*;
import com.rentacar.common.dto.PageResponse;
import com.rentacar.common.exception.CustomerAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Override
    public CustomerDetailResponse getById(Long id) {

        Customer customer = customerRepository.findById(id).orElse(null);

        CustomerDetailResponse dto = customerMapper.toDetail(customer);
        return dto;
    }

    @Override
    public PageResponse<CustomerSummaryResponse> search(
            CustomerSearchCriteria criteria, Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);

        Page<CustomerSummaryResponse> mapped = customers.map(this::toSummary);
        return PageResponse.from(mapped);
    }

    private CustomerSummaryResponse toSummary(Customer customer) {
        CustomerSummaryResponse dto = customerMapper.toSummary(customer);
        return dto;
    }

    @Override
    @Transactional // üç veritabanı işlemi var Hepsi tek bir işlem içinde olmalı
    public CustomerDetailResponse create(CreateCustomerRequest request) {

        // 1. İş Kuralı: aynı TCKN ile ikinci kayıt olmasın
        if (customerRepository.existsByNationalId(request.nationalId())) {
            throw new CustomerAlreadyExistsException(
                    "Bu TC kimlik numarası ile kayıtlı müşteri mevcut.");
        }

        // 2. İş Kuralı: email unique olmalı
        if (customerRepository.existsByEmail(request.email())) {
            throw new CustomerAlreadyExistsException(
                    "Bu e-posta adresi mevcut."
            );
        }

        // Request -> Entity
        Customer customer = customerMapper.toEntity(request);

        // Sunucunun belirlediği alanlar
        customer.setBlacklisted(false);

        // Kaydet - dönen nesneyi kullan, id burada oluşur
        Customer saved = customerRepository.save(customer);

        // Entity -> Response
        return customerMapper.toDetail(saved);
    }

    @Override
    public CustomerDetailResponse update(Long id, UpdateCustomerRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {
        CustomerDetailResponse byId = getById(id);

        if(byId != null){
            customerRepository.deleteById(byId.id());
        }

    }


}
