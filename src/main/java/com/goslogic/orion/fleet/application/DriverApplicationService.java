package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.Driver;
import com.goslogic.orion.fleet.domain.model.DriverStatus;
import com.goslogic.orion.fleet.domain.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DriverApplicationService {

    private final DriverRepository driverRepository;

    public DriverApplicationService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public record CreateDriverCommand(
            String externalId,
            String userExternalId,
            String tenantExternalId,
            String licenseNumber,
            String licenseCategory,
            LocalDate licenseExpiry
    ) {}

    public Driver create(CreateDriverCommand cmd) {
        if (driverRepository.existsByLicenseNumber(cmd.licenseNumber())) {
            throw new ConflictException("Ya existe un conductor con licencia: " + cmd.licenseNumber());
        }

        String externalId = cmd.externalId() != null
                ? cmd.externalId()
                : "driver-" + UUID.randomUUID().toString().substring(0, 8);

        Driver driver = new Driver(externalId, cmd.userExternalId(), cmd.tenantExternalId(),
                cmd.licenseNumber(), cmd.licenseCategory(), cmd.licenseExpiry());
        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public List<Driver> listByTenant(String tenantExternalId) {
        return driverRepository.findByTenantExternalId(tenantExternalId);
    }

    @Transactional(readOnly = true)
    public Driver findByExternalId(String externalId, String tenantExternalId) {
        return driverRepository
                .findByExternalIdAndTenantExternalId(externalId, tenantExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor no encontrado: " + externalId));
    }

    /**
     * Seam de resolución para el IAM (login de conductor).
     * Permite reemplazar la decisión D5 cuando Fleet esté integrado.
     */
    @Transactional(readOnly = true)
    public Driver resolveByUser(String userExternalId, String tenantExternalId) {
        return driverRepository
                .findByUserExternalIdAndTenantExternalId(userExternalId, tenantExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró conductor para el usuario: " + userExternalId));
    }

    public Driver updateStatus(String externalId, String tenantExternalId, DriverStatus status) {
        Driver d = findByExternalId(externalId, tenantExternalId);
        d.setStatus(status);
        return driverRepository.save(d);
    }

    public void delete(String externalId, String tenantExternalId) {
        Driver d = findByExternalId(externalId, tenantExternalId);
        driverRepository.delete(d);
    }
}
