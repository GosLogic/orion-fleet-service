package com.goslogic.orion.fleet.domain.repository;

import com.goslogic.orion.fleet.domain.model.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {

    List<VehicleDocument> findByVehicle_ExternalId(String vehicleExternalId);
}
