package com.goslogic.orion.fleet.presentation.rest;

import com.goslogic.orion.fleet.application.DriverApplicationService;
import com.goslogic.orion.fleet.application.DriverApplicationService.CreateDriverCommand;
import com.goslogic.orion.fleet.presentation.dto.CreateDriverRequest;
import com.goslogic.orion.fleet.presentation.dto.DriverResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/fleet/drivers")
@Tag(name = "Drivers", description = "Gestión de conductores de flota")
public class DriverController {

    private final DriverApplicationService driverService;

    public DriverController(DriverApplicationService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    @Operation(summary = "Listar conductores del tenant")
    public ResponseEntity<List<DriverResponse>> listByTenant(
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        return ResponseEntity.ok(driverService.listByTenant(tenantExternalId)
                .stream().map(DriverResponse::from).toList());
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener conductor por ID externo")
    public ResponseEntity<DriverResponse> findByExternalId(
            @PathVariable String externalId,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        return ResponseEntity.ok(DriverResponse.from(
                driverService.findByExternalId(externalId, tenantExternalId)));
    }

    /**
     * Seam de resolución para el IAM Service.
     * GET /v1/fleet/drivers/by-user?user_external_id={id}&tenant_id={tenant}
     * Permite que el IAM resuelva el driver_id real en el login (reemplaza decisión D5).
     */
    @GetMapping("/by-user")
    @Operation(summary = "Resolver conductor por user_external_id del IAM (seam de integración)")
    public ResponseEntity<DriverResponse> resolveByUser(
            @RequestParam("user_external_id") String userExternalId,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        return ResponseEntity.ok(DriverResponse.from(
                driverService.resolveByUser(userExternalId, tenantExternalId)));
    }

    @PostMapping
    @Operation(summary = "Registrar conductor")
    public ResponseEntity<DriverResponse> create(
            @Valid @RequestBody CreateDriverRequest req,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        CreateDriverCommand cmd = new CreateDriverCommand(
                req.externalId(),
                req.userExternalId(),
                tenantExternalId,
                req.licenseNumber(),
                req.licenseCategory(),
                req.licenseExpiry() != null ? LocalDate.parse(req.licenseExpiry()) : null
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DriverResponse.from(driverService.create(cmd)));
    }

    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar conductor")
    public ResponseEntity<Void> delete(
            @PathVariable String externalId,
            @RequestHeader("X-Tenant-Id") String tenantExternalId) {
        driverService.delete(externalId, tenantExternalId);
        return ResponseEntity.noContent().build();
    }
}
