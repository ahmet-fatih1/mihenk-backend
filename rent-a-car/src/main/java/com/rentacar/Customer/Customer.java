package com.rentacar.Customer;

import com.rentacar.Customer.entity.CustomerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    private String displayName;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "national_id", length = 11, unique = true)
    private String nationalId;

    @Column(name = "email", nullable = false, unique = true,length = 150)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "city")
    private String city;

    @Column(name = "ps_no")
    private String pasaportNo;

    @Column(name = "license_number", length = 30)
    private String licenseNumber;

    @Column(name = "license_issue_year")
    private Integer licenseIssueYear;

    @Column(name = "is_blacklisted")
    private boolean blacklisted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerType type;

    @Column(name = "tax_number", length = 10, unique = true)
    private String taxNumber;

    @Column(name = "blacklist_reason", length = 500)
    private String blacklistReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;




}
