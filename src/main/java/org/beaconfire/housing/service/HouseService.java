package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.repo.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HouseService {

    @Autowired
    private HouseRepository houseRepository;

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
}