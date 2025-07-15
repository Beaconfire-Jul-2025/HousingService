package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.repo.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    public Page<Facility> getFacilities(Integer houseId, String type, Integer quantity,
                                        Pageable pageable) {
        return facilityRepository.findByFilters(houseId, type, quantity, pageable);
    }


    public Facility getFacilityById(int id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Facility with ID " + id + " not found."));
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