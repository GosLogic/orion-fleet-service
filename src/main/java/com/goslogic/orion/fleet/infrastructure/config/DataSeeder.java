package com.goslogic.orion.fleet.infrastructure.config;

import com.goslogic.orion.fleet.domain.model.*;
import com.goslogic.orion.fleet.domain.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos demo alineados con el contrato móvil (movil.md §18 y §03-id-and-enum-mapping.md).
 *
 * Siembra:
 *   - VehicleType: "Furgoneta"
 *   - Vehicle: vehicle-001 (ABC-1234, Mercedes Sprinter 2024, tenant-demo)
 *   - Driver: driver-demo (user_external_id=driver-demo, tenant-demo)
 *   - Asigna driver-demo como defaultDriver de vehicle-001
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public DataSeeder(VehicleTypeRepository vehicleTypeRepository,
                      VehicleRepository vehicleRepository,
                      DriverRepository driverRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Tipo de vehículo (compartido)
        VehicleType furgoneta = vehicleTypeRepository.findByName("Furgoneta")
                .orElseGet(() -> vehicleTypeRepository.save(
                        new VehicleType("Furgoneta", "Vehículo de reparto mediano")));

        seedDemoTenant(furgoneta);
        seedBetaTenant(furgoneta);
    }

    private void seedDemoTenant(VehicleType furgoneta) {
        if (vehicleRepository.existsByExternalId("vehicle-001")) {
            log.info("[DataSeeder] Datos demo ya existentes — omitiendo seed");
            return;
        }

        // Conductor demo — external_id = user_external_id = "driver-demo"
        // compatible con IAM DataSeeder (User.externalId="driver-demo")
        Driver driverDemo = new Driver(
                "driver-demo",
                "driver-demo",         // user_external_id del IAM
                "tenant-demo",
                "LIC-DEMO-001",
                "A-IIIc",
                LocalDate.of(2027, 12, 31)
        );
        driverRepository.save(driverDemo);

        // Vehículo demo — external_id = "vehicle-001" (de movil.md §18)
        Vehicle vehicle001 = new Vehicle(
                "vehicle-001",
                "tenant-demo",
                furgoneta,
                "ABC-1234",
                "Mercedes",
                "Sprinter 2024",
                2024,
                new BigDecimal("3.50")
        );
        vehicle001.setDefaultDriverExternalId("driver-demo");
        vehicleRepository.save(vehicle001);

        log.info("[DataSeeder] Datos demo creados: vehicle-001 (ABC-1234), driver-demo");
    }

    /** Flota del segundo tenant para la demo de aislamiento multi-tenant (P0-3). Guard propio. */
    private void seedBetaTenant(VehicleType furgoneta) {
        if (vehicleRepository.existsByExternalId("vehicle-002")) {
            log.info("[DataSeeder] Datos beta ya existentes — omitiendo seed");
            return;
        }

        Driver driverBeta = new Driver(
                "driver-beta",
                "driver-beta",         // user_external_id del IAM (tenant-beta)
                "tenant-beta",
                "LIC-BETA-001",
                "A-IIIc",
                LocalDate.of(2028, 6, 30)
        );
        driverRepository.save(driverBeta);

        Vehicle vehicle002 = new Vehicle(
                "vehicle-002",
                "tenant-beta",
                furgoneta,
                "XYZ-9876",
                "Iveco",
                "Daily 2025",
                2025,
                new BigDecimal("4.20")
        );
        vehicle002.setDefaultDriverExternalId("driver-beta");
        vehicleRepository.save(vehicle002);

        log.info("[DataSeeder] Datos beta creados: vehicle-002 (XYZ-9876), driver-beta");
    }
}
