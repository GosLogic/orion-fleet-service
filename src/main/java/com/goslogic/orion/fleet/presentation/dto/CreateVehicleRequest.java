package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateVehicleRequest(
        String externalId,
        @JsonProperty("vehicle_type_id") Long vehicleTypeId,
        @NotBlank String plate,
        @NotBlank String brand,
        @NotBlank String model,
        Integer year,
        @JsonProperty("load_capacity_tn") BigDecimal loadCapacityTn
) {}
