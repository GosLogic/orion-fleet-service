package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.DriverApplicationService.CreateDriverCommand;
import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.Driver;
import com.goslogic.orion.fleet.domain.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DriverApplicationServiceTest {

    @Mock DriverRepository driverRepository;

    DriverApplicationService service;
    Driver driver;

    @BeforeEach
    void setUp() {
        service = new DriverApplicationService(driverRepository);

        driver = new Driver("driver-demo", "driver-demo", "tenant-demo",
                "LIC-DEMO-001", "A-IIIc", LocalDate.of(2027, 12, 31));

        when(driverRepository.existsByLicenseNumber("LIC-DEMO-001")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("LIC-DUPLICADA")).thenReturn(true);
        when(driverRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(driverRepository.findByExternalIdAndTenantExternalId("driver-demo", "tenant-demo"))
                .thenReturn(Optional.of(driver));
        when(driverRepository.findByExternalIdAndTenantExternalId("inexistente", "tenant-demo"))
                .thenReturn(Optional.empty());
        when(driverRepository.findByUserExternalIdAndTenantExternalId("driver-demo", "tenant-demo"))
                .thenReturn(Optional.of(driver));
        when(driverRepository.findByUserExternalIdAndTenantExternalId("user-xxx", "tenant-demo"))
                .thenReturn(Optional.empty());
    }

    private CreateDriverCommand cmd(String license) {
        return new CreateDriverCommand("driver-demo", "driver-demo", "tenant-demo",
                license, "A-IIIc", LocalDate.of(2027, 12, 31));
    }

    @Test
    void create_registra_conductor_correctamente() {
        Driver result = service.create(cmd("LIC-DEMO-001"));
        assertThat(result.getExternalId()).isEqualTo("driver-demo");
        assertThat(result.getUserExternalId()).isEqualTo("driver-demo");
    }

    @Test
    void create_lanza_409_si_licencia_duplicada() {
        assertThatThrownBy(() -> service.create(cmd("LIC-DUPLICADA")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("LIC-DUPLICADA");
    }

    @Test
    void findByExternalId_devuelve_conductor_existente() {
        Driver result = service.findByExternalId("driver-demo", "tenant-demo");
        assertThat(result.getLicenseNumber()).isEqualTo("LIC-DEMO-001");
    }

    @Test
    void findByExternalId_lanza_404_si_no_existe() {
        assertThatThrownBy(() -> service.findByExternalId("inexistente", "tenant-demo"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void resolveByUser_devuelve_conductor_por_user_external_id() {
        Driver result = service.resolveByUser("driver-demo", "tenant-demo");
        assertThat(result.getExternalId()).isEqualTo("driver-demo");
    }

    @Test
    void resolveByUser_lanza_404_si_no_hay_driver_para_el_usuario() {
        assertThatThrownBy(() -> service.resolveByUser("user-xxx", "tenant-demo"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("user-xxx");
    }
}
