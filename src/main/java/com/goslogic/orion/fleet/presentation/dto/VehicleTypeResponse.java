package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goslogic.orion.fleet.domain.model.VehicleType;

public record VehicleTypeResponse(
        Long id,
        String name,
        String description
) {
    public static VehicleTypeResponse from(VehicleType vt) {
        return new VehicleTypeResponse(vt.getId(), vt.getName(), vt.getDescription());
    }
}
