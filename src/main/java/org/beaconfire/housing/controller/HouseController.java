package org.beaconfire.housing.controller;


import org.beaconfire.housing.dto.HouseDTO;
import org.beaconfire.housing.dto.PageListResponse;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/house")
public class HouseController {

    @Autowired
    private HouseService houseService;


    @GetMapping
    public PageListResponse<House> getAllHouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer maxOccupant,
            @RequestParam(required = false) Integer currentOccupant,
            @RequestParam(required = false) Integer landlordId
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<House> houses = houseService.getHouses(address, maxOccupant, currentOccupant, landlordId, pageable);

        return PageListResponse.<House>builder()
                .list(houses.getContent())
                .current(houses.getNumber() + 1)
                .pageSize(houses.getSize())
                .total(houses.getTotalElements())
                .build();
    }

    @GetMapping("/{id}")
    public House getHouseById(@PathVariable int id) {
        return houseService.getHouseById(id);
    }

    @PostMapping
    public House createHouse(@Valid @RequestBody HouseDTO dto) {

        House house = new House();
        house.setLandlordId(dto.getLandlordId());
        house.setAddress(dto.getAddress());
        house.setMaxOccupant(dto.getMaxOccupant());
        house.setCurrentOccupant(0);
        house.setDescription(dto.getDescription());
        try{
            return houseService.createHouse(house);
        }
        catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public House updateHouse(@PathVariable int id, @RequestBody HouseDTO updatedHouse) {
        if (!houseService.houseExists(id)){
            throw new IllegalArgumentException("House does not exist.");
        }
        House house = new House();
        house.setId(id);
        house.setLandlordId(updatedHouse.getLandlordId());
        house.setAddress(updatedHouse.getAddress());
        house.setMaxOccupant(updatedHouse.getMaxOccupant());
        house.setDescription(updatedHouse.getDescription());
        return houseService.updateHouse(house);

    }

    @DeleteMapping("/{id}")
    public void deleteHouse(@PathVariable int id) {
        houseService.deleteHouseById(id);
    }

    @GetMapping("/{houseId}/current-occupant")
    public Integer getCurrentOccupant(@PathVariable Integer houseId) {
            return houseService.getCurrentOccupant(houseId);
    }


    @PostMapping("/{houseId}/current-occupant/increase")
    public Integer increaseOccupant(@PathVariable Integer houseId) {
        return houseService.incrementOccupant(houseId);
    }

    @PostMapping("/{houseId}/current-occupant/decrease")
    public Integer decreaseOccupant(@PathVariable Integer houseId) {

        return houseService.decrementOccupant(houseId);

    }

    @GetMapping("/available")
    public PageListResponse<House> get3AvailableHouses() {
        Sort sort = Sort.by("currentOccupant").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        Page<House> houses = houseService.getAvailableHouses(pageable);


        return PageListResponse.<House>builder()
                .list(houses.getContent())
                .current(houses.getNumber() + 1)
                .pageSize(3)
                .total(3)
                .build();
    }

    @PostMapping("/assign")
    public Integer assignHouse() {
        Sort sort = Sort.by("currentOccupant").descending();
        Pageable pageable = PageRequest.of(0, 1, sort);
        Page<House> houses = houseService.getAvailableHouses(pageable);
        Integer houseId = houses.getContent().get(0).getId();
        houseService.incrementOccupant(houseId);

        return houseId;
    }

}
