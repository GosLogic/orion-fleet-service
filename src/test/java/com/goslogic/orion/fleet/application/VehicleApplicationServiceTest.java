package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.VehicleApplicationService.CreateVehicleCommand;
import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.Vehicle;
import com.goslogic.orion.fleet.domain.model.VehicleStatus;
import com.goslogic.orion.fleet.domain.repository.VehicleRepository;
import com.goslogic.orion.fleet.domain.repository.VehicleTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleApplicationServiceTest {

    @Mock VehicleRepository vehicleRepository;
    @Mock VehicleTypeRepository vehicleTypeRepository;

    VehicleApplicationService service;

    Vehicle vehicle;

    @BeforeEach
    void setUp() {
        service = new VehicleApplicationService(vehicleRepository, vehicleTypeRepository);

        vehicle = new Vehicle("vehicle-001", "tenant-demo", null,
                "ABC-1234", "Mercedes", "Sprinter 2024", 2024, new BigDecimal("3.5"));

        when(vehicleRepository.existsByPlate("ABC-1234")).thenReturn(false);
        when(vehicleRepository.existsByPlate("DUPLICADA")).thenReturn(true);
        when(vehicleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(vehicleRepository.findByExternalIdAndTenantExternalId("vehicle-001", "tenant-demo"))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByExternalIdAndTenantExternalId("inexistente", "tenant-demo"))
                .thenReturn(Optional.empty());
        when(vehicleRepository.findByExternalId("vehicle-001"))
                .thenReturn(Optional.of(vehicle));
    }

    private CreateVehicleCommand cmd(String plate) {
        return new CreateVehicleCommand("vehicle-001", "tenant-demo", null,
                plate, "Mercedes", "Sprinter 2024", 2024, new BigDecimal("3.5"));
    }

    @Test
    void create_registra_vehiculo_correctamente() {
        Vehicle result = service.create(cmd("ABC-1234"));
        assertThat(result.getPlate()).isEqualTo("ABC-1234");
        assertThat(result.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    void create_lanza_409_si_placa_duplicada() {
        assertThatThrownBy(() -> service.create(cmd("DUPLICADA")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("DUPLICADA");
    }

    @Test
    void findByExternalId_devuelve_vehiculo_existente() {
        Vehicle result = service.findByExternalId("vehicle-001", "tenant-demo");
        assertThat(result.getExternalId()).isEqualTo("vehicle-001");
    }

    @Test
    void findByExternalId_lanza_404_si_no_existe() {
        assertThatThrownBy(() -> service.findByExternalId("inexistente", "tenant-demo"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void resolveByExternalId_resuelve_sin_filtro_de_tenant() {
        Vehicle result = service.resolveByExternalId("vehicle-001");
        assertThat(result.getExternalId()).isEqualTo("vehicle-001");
    }

    @Test
    void updateStatus_cambia_el_estado() {
        Vehicle result = service.updateStatus("vehicle-001", "tenant-demo", VehicleStatus.ON_ROUTE);
        assertThat(result.getStatus()).isEqualTo(VehicleStatus.ON_ROUTE);
    }
}
