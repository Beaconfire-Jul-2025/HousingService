package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.repo.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HouseService {

    @Autowired
    private HouseRepository houseRepository;

    @Transactional(readOnly = true)
    public House getHouseById(int id) {
        return houseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("House with ID " + id + " not found."));
    }

    @Transactional
    public void createHouse(House house) {
        houseRepository.save(house);
    }

    @Transactional
    public void deleteHouseById(int id) {
        houseRepository.deleteById(id);
    }

    @Transactional
    public void updateHouse(House house) {
        houseRepository.save(house);
    }

    @Transactional
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public boolean houseExists(int houseId) {
        return houseRepository.existsById(houseId);
    }

    public Page<House> getHouses(int page, int size, String sortBy, String sortDir, String address) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (address != null && !address.isEmpty()) {
            return houseRepository.findByAddressContainingIgnoreCase(address, pageable);
        }
        return houseRepository.findAll(pageable);
    }
}