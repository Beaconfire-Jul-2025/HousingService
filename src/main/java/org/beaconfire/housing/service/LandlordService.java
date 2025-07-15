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

    public Page<Landlord> getAllLandlords(int page, int size, String sortBy, String sortDir, String email) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (email != null && !email.isEmpty()) {
            return landlordRepository.findByEmailContainingIgnoreCase(email, pageable);
        }
        return landlordRepository.findAll(pageable);
    }

    public Landlord getLandlordById(Integer id) {
        return landlordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Landlord with ID " + id + " not found."));
    }

    public Landlord createLandlord(Landlord landlord) {
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