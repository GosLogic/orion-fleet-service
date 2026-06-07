package com.goslogic.orion.fleet.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_documents")
@Getter
@Setter
@NoArgsConstructor
public class VehicleDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 25)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, length = 50)
    private String documentNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DocumentStatus status = DocumentStatus.VALID;

    public VehicleDocument(Vehicle vehicle, DocumentType documentType,
                           String documentNumber, LocalDate expiryDate) {
        this.vehicle = vehicle;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.expiryDate = expiryDate;
    }
}
