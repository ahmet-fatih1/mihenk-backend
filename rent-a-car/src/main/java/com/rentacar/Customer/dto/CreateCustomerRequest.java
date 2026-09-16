package com.rentacar.Customer.dto;

import com.rentacar.common.enums.CustomerType;
import jakarta.validation.constraints.*;



public record CreateCustomerRequest(
        @NotNull CustomerType type,
        @NotBlank(message = "isim boş olamaz!") @Size(max = 100) String firstName,
        @Size(max = 100) String surname,
        @Size(max = 200) String displayName,
        @Size(max = 200) String companyName,
        @Size(max = 20, message = "Pasaport numarası en fazla 20 karakter")
        String pasaportNo,
        @Pattern(regexp = "\\d{11}", message = "{validation.nationalId.invalid}")
        String nationalId,
        String taxNumber,
        @Email String email,
        @NotBlank @Size(max = 30) String phone,
        String licenseNumber,
        Integer licenseIssueYear,
        String city
) {}
