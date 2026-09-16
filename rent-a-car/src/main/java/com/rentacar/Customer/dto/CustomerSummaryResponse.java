package com.rentacar.Customer.dto;

public record CustomerSummaryResponse(
        Long id,
        String displayName,   // bireysel → ad soyad, kurumsal → firma adı
        String phone,
        String city,
        boolean blacklisted
) {}

