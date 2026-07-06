package com.goslogic.orion.fleet.presentation.rest;

import com.goslogic.orion.fleet.application.VehicleApplicationService;
import com.goslogic.orion.fleet.application.VehicleApplicationService.CreateVehicleCommand;
import com.goslogic.orion.fleet.application.VehicleDocumentApplicationService;
import com.goslogic.orion.fleet.domain.model.DocumentType;
import com.goslogic.orion.fleet.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/fleet/vehicles")
@Tag(name = "Vehicles", description = "Gestión de vehículos de la flota")
public class VehicleController {

    private final VehicleApplicationService vehicleService;
    private final VehicleDocumentApplicationService documentService;

    public VehicleController(VehicleApplicationService vehicleService,
                             VehicleDocumentApplicationService documentService) {
        this.vehicleService = vehicleService;
        this.documentService = documentService;
    }

    @GetMapping
    @Operation(summary = "Listar vehículos del tenant")
    public ResponseEntity<List<VehicleResponse>> listByTenant(
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        return ResponseEntity.ok(vehicleService.listByTenant(tenantExternalId)
                .stream().map(VehicleResponse::from).toList());
    }

    /**
     * Seam de resolución para IAM Service en login DRIVER.
     * GET /v1/fleet/vehicles/by-driver?driver_external_id=driver-demo
     */
    @GetMapping("/by-driver")
    @Operation(summary = "Resolver vehículo asignado al conductor (seam IAM login)")
    public ResponseEntity<VehicleResponse> resolveByDriver(
            @RequestParam("driver_external_id") String driverExternalId,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        return ResponseEntity.ok(VehicleResponse.from(
                vehicleService.resolveByDefaultDriver(driverExternalId, tenantExternalId)));
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener vehículo por ID externo (incluye resolución interna sin filtro de tenant)")
    public ResponseEntity<VehicleResponse> findByExternalId(
            @PathVariable String externalId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantExternalId) {
        if (tenantExternalId != null) {
            return ResponseEntity.ok(VehicleResponse.from(
                    vehicleService.findByExternalId(externalId, tenantExternalId)));
        }
        return ResponseEntity.ok(VehicleResponse.from(
                vehicleService.resolveByExternalId(externalId)));
    }

    @PostMapping
    @Operation(summary = "Crear vehículo")
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody CreateVehicleRequest req,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        CreateVehicleCommand cmd = new CreateVehicleCommand(
                req.externalId(), tenantExternalId, req.vehicleTypeId(),
                req.plate(), req.brand(), req.model(), req.year(), req.loadCapacityTn());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VehicleResponse.from(vehicleService.create(cmd)));
    }

    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar vehículo")
    public ResponseEntity<Void> delete(
            @PathVariable String externalId,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        vehicleService.delete(externalId, tenantExternalId);
        return ResponseEntity.noContent().build();
    }

    // --- Subrecurso: documentos legales ---

    @GetMapping("/{externalId}/documents")
    @Operation(summary = "Listar documentos del vehículo (SOAT, revisión técnica, etc.)")
    public ResponseEntity<List<VehicleDocumentResponse>> listDocuments(
            @PathVariable String externalId) {
        return ResponseEntity.ok(documentService.listByVehicle(externalId)
                .stream().map(VehicleDocumentResponse::from).toList());
    }

    @PostMapping("/{externalId}/documents")
    @Operation(summary = "Agregar documento al vehículo")
    public ResponseEntity<VehicleDocumentResponse> addDocument(
            @PathVariable String externalId,
            @Valid @RequestBody AddDocumentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VehicleDocumentResponse.from(
                        documentService.addDocument(
                                externalId,
                                DocumentType.valueOf(req.documentType().toUpperCase()),
                                req.documentNumber(),
                                LocalDate.parse(req.expiryDate())
                        )));
    }
}
