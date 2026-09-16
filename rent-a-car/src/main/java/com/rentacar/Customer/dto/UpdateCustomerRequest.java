package com.rentacar.Customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateCustomerRequest(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 200) String companyName,
        @Pattern(regexp = "\\d{11}", message = "{validation.nationalId.invalid}")
        String taxNumber,
        @Email String email,
        @NotBlank @Size(max = 30) String phone,
        LocalDate birthDate,
        String licenseNo,
        String licenseClass,
        LocalDate licenseIssueDate,
        String address,
        String city,
        String notes
) {}
