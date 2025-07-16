package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.repo.LandlordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class LandlordService {

    @Autowired
    private LandlordRepository landlordRepository;

    public Page<Landlord> getAllLandlords(String firstName, String lastName, String email, String cellPhone, Pageable pageable) {
        return landlordRepository.findByFilters(firstName, lastName, email, cellPhone, pageable);
    }

    public Landlord getLandlordById(Integer id) {
        return landlordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Landlord with ID " + id + " not found."));
    }

    public Landlord createLandlord(Landlord landlord) {
        if (landlord == null) {
            throw new IllegalArgumentException("Landlord cannot be null");
        }
        return landlordRepository.save(landlord);
    }

    public Landlord updateLandlord(Integer id, Landlord updated) {
        return landlordRepository.findById(id).map(existing -> {
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setEmail(updated.getEmail());
            existing.setCellPhone(updated.getCellPhone());
            return landlordRepository.save(existing);
        }).orElseThrow(() -> new NoSuchElementException("Landlord not found"));
    }

    public void deleteLandlord(Integer id) {
        landlordRepository.deleteById(id);
    }
}