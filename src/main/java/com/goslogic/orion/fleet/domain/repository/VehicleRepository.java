package com.goslogic.orion.fleet.domain.repository;

import com.goslogic.orion.fleet.domain.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByExternalIdAndTenantExternalId(String externalId, String tenantExternalId);

    Optional<Vehicle> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);

    boolean existsByPlate(String plate);

    List<Vehicle> findByTenantExternalId(String tenantExternalId);

    Optional<Vehicle> findByDefaultDriverExternalIdAndTenantExternalId(
            String defaultDriverExternalId, String tenantExternalId);
}
