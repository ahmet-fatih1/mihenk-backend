package com.rentacar.Customer.dto;

import com.rentacar.Customer.entity.CustomerType;

public record CustomerSearchCriteria(
        String query,                   // isim/telefon/e-posta içinde arama
        CustomerType type,
        String city,
        Boolean blacklisted
) {}

