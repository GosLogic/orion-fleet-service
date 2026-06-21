package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.VehicleType;
import com.goslogic.orion.fleet.domain.repository.VehicleTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleTypeApplicationServiceTest {

    @Mock VehicleTypeRepository vehicleTypeRepository;

    VehicleTypeApplicationService service;
    VehicleType furgoneta;

    @BeforeEach
    void setUp() {
        service = new VehicleTypeApplicationService(vehicleTypeRepository);

        furgoneta = new VehicleType("Furgoneta", "Vehículo de reparto mediano");
        furgoneta.setId(1L);

        when(vehicleTypeRepository.existsByName("Furgoneta")).thenReturn(false);
        when(vehicleTypeRepository.existsByName("Camión")).thenReturn(true);
        when(vehicleTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(vehicleTypeRepository.findById(1L)).thenReturn(Optional.of(furgoneta));
        when(vehicleTypeRepository.findById(99L)).thenReturn(Optional.empty());
        when(vehicleTypeRepository.findAll()).thenReturn(List.of(furgoneta));
    }

    @Test
    void create_registra_tipo_correctamente() {
        VehicleType result = service.create("Furgoneta", "Vehículo de reparto mediano");

        assertThat(result.getName()).isEqualTo("Furgoneta");
        assertThat(result.getDescription()).isEqualTo("Vehículo de reparto mediano");
        verify(vehicleTypeRepository).save(any(VehicleType.class));
    }

    @Test
    void create_lanza_409_si_nombre_duplicado() {
        assertThatThrownBy(() -> service.create("Camión", "Carga pesada"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Camión");
    }

    @Test
    void findAll_devuelve_lista_de_tipos() {
        List<VehicleType> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Furgoneta");
    }

    @Test
    void findById_devuelve_tipo_existente() {
        VehicleType result = service.findById(1L);

        assertThat(result.getName()).isEqualTo("Furgoneta");
    }

    @Test
    void findById_lanza_404_si_no_existe() {
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_modifica_nombre_y_descripcion() {
        VehicleType result = service.update(1L, "Furgoneta", "Descripción actualizada");

        assertThat(result.getDescription()).isEqualTo("Descripción actualizada");
        verify(vehicleTypeRepository).save(furgoneta);
    }

    @Test
    void update_lanza_409_si_nuevo_nombre_ya_existe() {
        assertThatThrownBy(() -> service.update(1L, "Camión", "Otro tipo"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Camión");
    }

    @Test
    void update_permite_mismo_nombre_sin_conflicto() {
        VehicleType result = service.update(1L, "Furgoneta", "Nueva descripción");

        assertThat(result.getName()).isEqualTo("Furgoneta");
        assertThat(result.getDescription()).isEqualTo("Nueva descripción");
    }

    @Test
    void delete_elimina_tipo_existente() {
        service.delete(1L);

        verify(vehicleTypeRepository).delete(furgoneta);
    }

    @Test
    void delete_lanza_404_si_no_existe() {
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
