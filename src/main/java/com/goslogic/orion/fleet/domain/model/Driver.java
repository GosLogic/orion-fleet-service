package com.goslogic.orion.fleet.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, nullable = false, length = 100)
    private String externalId;

    /**
     * Referencia al User del IAM por external_id (cross-DB, no FK real).
     * Permite resolver el conductor desde el login del IAM (seam D5).
     */
    @Column(name = "user_external_id", unique = true, nullable = false, length = 100)
    private String userExternalId;

    @Column(name = "tenant_external_id", nullable = false, length = 100)
    private String tenantExternalId;

    @Column(name = "license_number", unique = true, nullable = false, length = 20)
    private String licenseNumber;

    @Column(name = "license_category", length = 10)
    private String licenseCategory;

    @Column(name = "license_expiry")
    private LocalDate licenseExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status = DriverStatus.AVAILABLE;

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

    public Driver(String externalId, String userExternalId, String tenantExternalId,
                  String licenseNumber, String licenseCategory, LocalDate licenseExpiry) {
        this.externalId = externalId;
        this.userExternalId = userExternalId;
        this.tenantExternalId = tenantExternalId;
        this.licenseNumber = licenseNumber;
        this.licenseCategory = licenseCategory;
        this.licenseExpiry = licenseExpiry;
    }
}
