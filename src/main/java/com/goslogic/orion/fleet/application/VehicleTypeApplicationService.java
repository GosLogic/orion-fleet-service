package com.goslogic.orion.fleet.application;

import com.goslogic.orion.fleet.application.exception.ConflictException;
import com.goslogic.orion.fleet.application.exception.ResourceNotFoundException;
import com.goslogic.orion.fleet.domain.model.VehicleType;
import com.goslogic.orion.fleet.domain.repository.VehicleTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VehicleTypeApplicationService {

    private final VehicleTypeRepository vehicleTypeRepository;

    public VehicleTypeApplicationService(VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public VehicleType create(String name, String description) {
        if (vehicleTypeRepository.existsByName(name)) {
            throw new ConflictException("Ya existe un tipo de vehículo con nombre: " + name);
        }
        return vehicleTypeRepository.save(new VehicleType(name, description));
    }

    @Transactional(readOnly = true)
    public List<VehicleType> findAll() {
        return vehicleTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public VehicleType findById(Long id) {
        return vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType no encontrado: " + id));
    }

    public VehicleType update(Long id, String name, String description) {
        VehicleType vt = findById(id);
        if (!vt.getName().equals(name) && vehicleTypeRepository.existsByName(name)) {
            throw new ConflictException("Ya existe un tipo de vehículo con nombre: " + name);
        }
        vt.setName(name);
        vt.setDescription(description);
        return vehicleTypeRepository.save(vt);
    }

    public void delete(Long id) {
        VehicleType vt = findById(id);
        vehicleTypeRepository.delete(vt);
    }
}
