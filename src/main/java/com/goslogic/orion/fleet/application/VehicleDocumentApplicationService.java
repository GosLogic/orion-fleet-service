package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.DocumentType;
import com.goslogic.orion.fleet.domain.model.Vehicle;
import com.goslogic.orion.fleet.domain.model.VehicleDocument;
import com.goslogic.orion.fleet.domain.repository.VehicleDocumentRepository;
import com.goslogic.orion.fleet.domain.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class VehicleDocumentApplicationService {

    private final VehicleDocumentRepository documentRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleDocumentApplicationService(VehicleDocumentRepository documentRepository,
                                             VehicleRepository vehicleRepository) {
        this.documentRepository = documentRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleDocument addDocument(String vehicleExternalId, DocumentType type,
                                       String documentNumber, LocalDate expiryDate) {
        Vehicle vehicle = vehicleRepository.findByExternalId(vehicleExternalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehículo no encontrado: " + vehicleExternalId));
        VehicleDocument doc = new VehicleDocument(vehicle, type, documentNumber, expiryDate);
        return documentRepository.save(doc);
    }

    @Transactional(readOnly = true)
    public List<VehicleDocument> listByVehicle(String vehicleExternalId) {
        return documentRepository.findByVehicle_ExternalId(vehicleExternalId);
    }
}
