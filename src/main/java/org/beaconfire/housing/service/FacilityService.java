package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.repo.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Facility getFacilityById(int id) {
        return facilityRepository.findById(id).orElse(null);
    }

    public Facility createFacility(Facility facility) {
        return facilityRepository.save(facility);
    }

    public Facility updateFacility(int id, Facility updatedFacility) {
        Facility existing = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facility with ID " + id + " not found."));

        existing.setType(updatedFacility.getType());
        existing.setQuantity(updatedFacility.getQuantity());
        existing.setDescription(updatedFacility.getDescription());
        existing.setHouse(updatedFacility.getHouse());

        return facilityRepository.save(existing);
    }

    public void deleteFacility(int id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facility with ID " + id + " not found."));

        facilityRepository.delete(facility);
    }
}