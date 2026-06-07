package com.goslogic.orion.fleet.domain.repository;

import com.goslogic.orion.fleet.domain.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    Optional<VehicleType> findByName(String name);
    boolean existsByName(String name);
}
