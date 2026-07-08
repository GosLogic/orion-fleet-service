package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.domain.model.VehicleType;
import com.goslogic.orion.fleet.domain.repository.VehicleTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
class VehicleTypeApplicationServiceTest {

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    private VehicleTypeApplicationService service;
    private VehicleType furgoneta;

    @BeforeEach
    void setUp() {
        service = new VehicleTypeApplicationService(vehicleTypeRepository);

        furgoneta = new VehicleType("Furgoneta", "Descripción antigua");
        furgoneta.setId(1L);

        when(vehicleTypeRepository.existsByName("Furgoneta")).thenReturn(false);
        when(vehicleTypeRepository.existsByName("Camión")).thenReturn(true);
        when(vehicleTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.of(furgoneta));
    }

    @Test
    void create_lanza_409_si_nombre_duplicado() {
        assertThatThrownBy(() -> service.create("Camión", "Carga pesada"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Camión");
    }

    @Test
    void update_modifica_nombre_y_descripcion() {
        VehicleType result = service.update(1L, "Furgoneta", "Descripción nueva");

        assertThat(result.getName()).isEqualTo("Furgoneta");
        assertThat(result.getDescription()).isEqualTo("Descripción nueva");
    }
}
