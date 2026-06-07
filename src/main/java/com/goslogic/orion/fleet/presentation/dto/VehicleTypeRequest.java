package com.goslogic.orion.fleet.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record VehicleTypeRequest(
        @NotBlank String name,
        String description
) {}
