package com.goslogic.orion.fleet.presentation.rest;

import com.goslogic.orion.fleet.application.VehicleTypeApplicationService;
import com.goslogic.orion.fleet.presentation.dto.VehicleTypeRequest;
import com.goslogic.orion.fleet.presentation.dto.VehicleTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fleet/vehicle-types")
@Tag(name = "Vehicle Types", description = "Catálogo de tipos de vehículo")
public class VehicleTypeController {

    private final VehicleTypeApplicationService vehicleTypeService;

    public VehicleTypeController(VehicleTypeApplicationService vehicleTypeService) {
        this.vehicleTypeService = vehicleTypeService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los tipos de vehículo")
    public ResponseEntity<List<VehicleTypeResponse>> findAll() {
        return ResponseEntity.ok(vehicleTypeService.findAll().stream()
                .map(VehicleTypeResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de vehículo por ID")
    public ResponseEntity<VehicleTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(VehicleTypeResponse.from(vehicleTypeService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear tipo de vehículo")
    public ResponseEntity<VehicleTypeResponse> create(@Valid @RequestBody VehicleTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VehicleTypeResponse.from(
                        vehicleTypeService.create(req.name(), req.description())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de vehículo")
    public ResponseEntity<VehicleTypeResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody VehicleTypeRequest req) {
        return ResponseEntity.ok(VehicleTypeResponse.from(
                vehicleTypeService.update(id, req.name(), req.description())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de vehículo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
