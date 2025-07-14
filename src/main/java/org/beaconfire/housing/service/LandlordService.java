package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.exception.UserNotFoundException;
import org.beaconfire.housing.repo.LandlordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LandlordService {

    @Autowired
    private LandlordRepository landlordRepository;

    public List<Landlord> getAllLandlords() {
        return landlordRepository.findAll();
    }

    public Optional<Landlord> getLandlordById(Integer id) {
        return landlordRepository.findById(id);
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
        }).orElseThrow(() -> new UserNotFoundException("Landlord not found"));
    }

    public void deleteLandlord(Integer id) {
        landlordRepository.deleteById(id);
    }
}