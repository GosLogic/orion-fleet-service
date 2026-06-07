package com.goslogic.orion.fleet.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.goslogic.orion.fleet.domain.model.Vehicle;

import java.math.BigDecimal;

public record VehicleResponse(
        @JsonProperty("id")              String externalId,
        @JsonProperty("tenant_id")       String tenantExternalId,
        @JsonProperty("vehicle_type_id") Long vehicleTypeId,
        String plate,
        String brand,
        String model,
        Integer year,
        @JsonProperty("load_capacity_tn") BigDecimal loadCapacityTn,
        String status,
        @JsonProperty("virtual_odometer") BigDecimal virtualOdometer,
        @JsonProperty("default_driver_id") String defaultDriverExternalId
) {
    public static VehicleResponse from(Vehicle v) {
        return new VehicleResponse(
                v.getExternalId(),
                v.getTenantExternalId(),
                v.getVehicleType() != null ? v.getVehicleType().getId() : null,
                v.getPlate(),
                v.getBrand(),
                v.getModel(),
                v.getYear(),
                v.getLoadCapacityTn(),
                v.getStatus().name(),
                v.getVirtualOdometer(),
                v.getDefaultDriverExternalId()
        );
    }
}
