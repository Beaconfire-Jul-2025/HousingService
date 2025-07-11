package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.repo.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HouseService {

    @Autowired
    private HouseRepository houseRepository;

    @Transactional
    public void createHouse(House house) {
        houseRepository.saveHouse(house);
    }
}