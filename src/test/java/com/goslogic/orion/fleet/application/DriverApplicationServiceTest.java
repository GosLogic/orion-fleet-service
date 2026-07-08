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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Estas pruebas garantizan la Integridad de datos de flota y cumplimiento normativo en un entorno multi-tenant.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DriverApplicationServiceTest {

    @Mock
    private DriverRepository driverRepository;

    private DriverApplicationService service;

    @BeforeEach
    void setUp() {
        service = new DriverApplicationService(driverRepository);
        when(driverRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void create_registra_conductor_correctamente() {
        when(driverRepository.existsByLicenseNumber("LIC-001")).thenReturn(false);

        CreateDriverCommand cmd = new CreateDriverCommand(
                "driver-001",
                "user-001",
                "tenant-demo",
                "LIC-001",
                "A-IIIc",
                LocalDate.of(2028, 1, 1)
        );

        Driver result = service.create(cmd);
        assertThat(result.getExternalId()).isEqualTo("driver-001");
        assertThat(result.getUserExternalId()).isEqualTo("user-001");
        assertThat(result.getTenantExternalId()).isEqualTo("tenant-demo");
    }

    @Test
    void create_lanza_409_si_licencia_duplicada() {
        when(driverRepository.existsByLicenseNumber("LIC-DUP")).thenReturn(true);

        CreateDriverCommand cmd = new CreateDriverCommand(
                "driver-dup",
                "user-dup",
                "tenant-demo",
                "LIC-DUP",
                "A-II",
                LocalDate.of(2028, 1, 1)
        );

        assertThatThrownBy(() -> service.create(cmd))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("LIC-DUP");
    }

    @Test
    void resolveByUser_devuelve_conductor_por_user_external_id() {
        Driver driver = new Driver("driver-iam", "user-iam", "tenant-demo", "LIC-009", "A-IIIc", null);
        when(driverRepository.findByUserExternalIdAndTenantExternalId("user-iam", "tenant-demo"))
                .thenReturn(Optional.of(driver));

        Driver result = service.resolveByUser("user-iam", "tenant-demo");
        assertThat(result.getExternalId()).isEqualTo("driver-iam");
    }

    @Test
    void findByExternalId_lanza_404_si_no_existe() {
        when(driverRepository.findByExternalIdAndTenantExternalId("driver-no-existe", "tenant-demo"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByExternalId("driver-no-existe", "tenant-demo"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
