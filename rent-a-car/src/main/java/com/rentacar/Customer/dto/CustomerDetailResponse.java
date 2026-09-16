package com.rentacar.Customer.dto;


import com.rentacar.Customer.entity.CustomerType;
import java.time.Instant;
import java.time.LocalDate;

public record CustomerDetailResponse(
        Long id,
        CustomerType type,
        String firstName,
        String surname,
        String companyName,
        String nationalIdMasked,   // "123******89"
        String taxNumber,
        String email,
        String phone,
        String licenseNumber,
        Integer licenseIssueYear,
        String city,
        boolean blacklisted,
        String blacklistReason,
        Instant createdAt,
        Instant updatedAt
) {}
