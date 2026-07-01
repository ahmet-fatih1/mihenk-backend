package com.rentacar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "tc_no", nullable = false)
    private String tcNo;

    @Column(name = "ps_no")
    private String pasaportNo;

    @Column(name = "license_number",nullable = false)
    private String licenseNumber;

    @Column(name = "license_issue_year")
    private String licenseIssueYear;

    @Column(name = "is_blacklisted")
    private Boolean isBlacklisted;



}
