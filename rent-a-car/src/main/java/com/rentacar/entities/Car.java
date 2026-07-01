package com.rentacar.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rent_price", nullable = false)
    private double rentPrice;

    @Column(name = "plate_number", unique = true, nullable = false)
    private String plateNumber;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "model_year")
    private int modelYear;

    @Column(name = "current_kilometer", nullable = false)
    private int currentKilometer;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.AVAILABLE;

    @Column(name = "vize_bitis_tarihi", nullable = false)
    private LocalDate vizeBitisTarihi;

    @Column(name = "kasko_bitis_tarihi", nullable = false)
    private LocalDate kaskoBitisTarihi;



}
