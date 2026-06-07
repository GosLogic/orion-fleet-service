package com.goslogic.orion.fleet.domain.repository;

import com.goslogic.orion.fleet.domain.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByExternalIdAndTenantExternalId(String externalId, String tenantExternalId);

    Optional<Driver> findByExternalId(String externalId);

    Optional<Driver> findByUserExternalIdAndTenantExternalId(String userExternalId, String tenantExternalId);

    boolean existsByExternalId(String externalId);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByTenantExternalId(String tenantExternalId);
}
