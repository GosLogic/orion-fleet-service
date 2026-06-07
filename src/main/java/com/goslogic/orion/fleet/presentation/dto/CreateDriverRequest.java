package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateDriverRequest(
        @JsonProperty("external_id")     String externalId,
        @NotBlank @JsonProperty("user_external_id")   String userExternalId,
        @NotBlank @JsonProperty("license_number")     String licenseNumber,
        @JsonProperty("license_category") String licenseCategory,
        @JsonProperty("license_expiry")   String licenseExpiry
) {}
