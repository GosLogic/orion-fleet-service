package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goslogic.orion.fleet.domain.model.VehicleDocument;

public record VehicleDocumentResponse(
        Long id,
        @JsonProperty("vehicle_id")      String vehicleExternalId,
        @JsonProperty("document_type")   String documentType,
        @JsonProperty("document_number") String documentNumber,
        @JsonProperty("expiry_date")     String expiryDate,
        String status
) {
    public static VehicleDocumentResponse from(VehicleDocument d) {
        return new VehicleDocumentResponse(
                d.getId(),
                d.getVehicle().getExternalId(),
                d.getDocumentType().name(),
                d.getDocumentNumber(),
                d.getExpiryDate() != null ? d.getExpiryDate().toString() : null,
                d.getStatus().name()
        );
    }
}
