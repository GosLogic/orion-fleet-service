package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.Vehicle;
import com.goslogic.orion.fleet.domain.model.VehicleStatus;
import com.goslogic.orion.fleet.domain.model.VehicleType;
import com.goslogic.orion.fleet.domain.repository.VehicleRepository;
import com.goslogic.orion.fleet.domain.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VehicleApplicationService {

    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    public VehicleApplicationService(VehicleRepository vehicleRepository,
                                     VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public record CreateVehicleCommand(
            String externalId,
            String tenantExternalId,
            Long vehicleTypeId,
            String plate,
            String brand,
            String model,
            Integer year,
            BigDecimal loadCapacityTn
    ) {}

    public Vehicle create(CreateVehicleCommand cmd) {
        if (vehicleRepository.existsByPlate(cmd.plate())) {
            throw new ConflictException("Ya existe un vehículo con placa: " + cmd.plate());
        }

        String externalId = cmd.externalId() != null
                ? cmd.externalId()
                : "vehicle-" + UUID.randomUUID().toString().substring(0, 8);

        VehicleType vehicleType = null;
        if (cmd.vehicleTypeId() != null) {
            vehicleType = vehicleTypeRepository.findById(cmd.vehicleTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "VehicleType no encontrado: " + cmd.vehicleTypeId()));
        }

        Vehicle vehicle = new Vehicle(externalId, cmd.tenantExternalId(), vehicleType,
                cmd.plate(), cmd.brand(), cmd.model(), cmd.year(), cmd.loadCapacityTn());
        return vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> listByTenant(String tenantExternalId) {
        return vehicleRepository.findByTenantExternalId(tenantExternalId);
    }

    @Transactional(readOnly = true)
    public Vehicle findByExternalId(String externalId, String tenantExternalId) {
        return vehicleRepository
                .findByExternalIdAndTenantExternalId(externalId, tenantExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehículo no encontrado: " + externalId));
    }

    /** Seam de resolución para Dispatch (sin filtro de tenant — resolución interna entre servicios). */
    @Transactional(readOnly = true)
    public Vehicle resolveByExternalId(String externalId) {
        return vehicleRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehículo no encontrado: " + externalId));
    }

    public Vehicle updateStatus(String externalId, String tenantExternalId, VehicleStatus status) {
        Vehicle v = findByExternalId(externalId, tenantExternalId);
        v.setStatus(status);
        return vehicleRepository.save(v);
    }

    public Vehicle assignDefaultDriver(String vehicleExternalId, String tenantExternalId,
                                       String driverExternalId) {
        Vehicle v = findByExternalId(vehicleExternalId, tenantExternalId);
        v.setDefaultDriverExternalId(driverExternalId);
        return vehicleRepository.save(v);
    }

    public void delete(String externalId, String tenantExternalId) {
        Vehicle v = findByExternalId(externalId, tenantExternalId);
        vehicleRepository.delete(v);
    }
}
