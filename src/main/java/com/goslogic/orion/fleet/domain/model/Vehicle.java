package com.goslogic.orion.fleet.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, nullable = false, length = 100)
    private String externalId;

    @Column(name = "tenant_external_id", nullable = false, length = 100)
    private String tenantExternalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;

    @Column(nullable = false, unique = true, length = 10)
    private String plate;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column
    private Integer year;

    @Column(name = "load_capacity_tn", precision = 10, scale = 2)
    private BigDecimal loadCapacityTn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "virtual_odometer", precision = 15, scale = 2)
    private BigDecimal virtualOdometer = BigDecimal.ZERO;

    /** Conductor asignado por defecto (FK lógica via externalId, cross-DB). */
    @Column(name = "default_driver_external_id", length = 100)
    private String defaultDriverExternalId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Vehicle(String externalId, String tenantExternalId, VehicleType vehicleType,
                   String plate, String brand, String model, Integer year,
                   BigDecimal loadCapacityTn) {
        this.externalId = externalId;
        this.tenantExternalId = tenantExternalId;
        this.vehicleType = vehicleType;
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.loadCapacityTn = loadCapacityTn;
    }
}
