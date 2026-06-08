package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.DocumentType;
import com.goslogic.orion.fleet.domain.model.Vehicle;
import com.goslogic.orion.fleet.domain.model.VehicleDocument;
import com.goslogic.orion.fleet.domain.repository.VehicleDocumentRepository;
import com.goslogic.orion.fleet.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleDocumentApplicationServiceTest {

    @Mock VehicleDocumentRepository documentRepository;
    @Mock VehicleRepository vehicleRepository;

    VehicleDocumentApplicationService service;
    Vehicle vehicle;

    @BeforeEach
    void setUp() {
        service = new VehicleDocumentApplicationService(documentRepository, vehicleRepository);

        vehicle = new Vehicle("vehicle-001", "tenant-demo", null,
                "ABC-1234", "Mercedes", "Sprinter 2024", 2024, new BigDecimal("3.5"));

        when(vehicleRepository.findByExternalId("vehicle-001")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByExternalId("inexistente")).thenReturn(Optional.empty());
        when(documentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(documentRepository.findByVehicle_ExternalId("vehicle-001")).thenReturn(List.of(
                new VehicleDocument(vehicle, DocumentType.SOAT, "SOAT-2026-001", LocalDate.of(2026, 12, 31))
        ));
    }

    @Test
    void addDocument_registra_documento_correctamente() {
        VehicleDocument result = service.addDocument(
                "vehicle-001", DocumentType.SOAT, "SOAT-2026-001", LocalDate.of(2026, 12, 31));

        assertThat(result.getDocumentType()).isEqualTo(DocumentType.SOAT);
        assertThat(result.getDocumentNumber()).isEqualTo("SOAT-2026-001");
        assertThat(result.getVehicle()).isEqualTo(vehicle);
        verify(documentRepository).save(any(VehicleDocument.class));
    }

    @Test
    void addDocument_lanza_404_si_vehiculo_no_existe() {
        assertThatThrownBy(() -> service.addDocument(
                "inexistente", DocumentType.SOAT, "SOAT-001", LocalDate.of(2026, 12, 31)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("inexistente");
    }

    @Test
    void listByVehicle_devuelve_documentos_del_vehiculo() {
        List<VehicleDocument> result = service.listByVehicle("vehicle-001");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDocumentType()).isEqualTo(DocumentType.SOAT);
        verify(documentRepository).findByVehicle_ExternalId("vehicle-001");
    }
}
