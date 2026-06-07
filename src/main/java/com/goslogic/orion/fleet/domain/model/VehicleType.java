package com.goslogic.orion.fleet.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle_types")
@Getter
@Setter
@NoArgsConstructor
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    public VehicleType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
