package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goslogic.orion.fleet.domain.model.Driver;

public record DriverResponse(
        @JsonProperty("id")               String externalId,
        @JsonProperty("user_external_id") String userExternalId,
        @JsonProperty("tenant_id")        String tenantExternalId,
        @JsonProperty("license_number")   String licenseNumber,
        @JsonProperty("license_category") String licenseCategory,
        @JsonProperty("license_expiry")   String licenseExpiry,
        String status
) {
    public static DriverResponse from(Driver d) {
        return new DriverResponse(
                d.getExternalId(),
                d.getUserExternalId(),
                d.getTenantExternalId(),
                d.getLicenseNumber(),
                d.getLicenseCategory(),
                d.getLicenseExpiry() != null ? d.getLicenseExpiry().toString() : null,
                d.getStatus().name()
        );
    }
}
