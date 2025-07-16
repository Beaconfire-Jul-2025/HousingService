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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import org.springframework.data.domain.Page;

@RestController
@PreAuthorize("hasRole('HR') or hasRole('COMPOSITE')")
@RequestMapping("/house")
public class HouseController {

    @Autowired
    private HouseService houseService;


    @GetMapping
    public PageListResponse<House> getAllHouses(
            Authentication authentication,
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
    public House getHouseById(@PathVariable int id, Authentication authentication) {
        return houseService.getHouseById(id);
    }

    @PostMapping
    public House createHouse(@Valid @RequestBody HouseDTO dto, Authentication authentication) {

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
    public House updateHouse(@PathVariable int id, @RequestBody HouseDTO updatedHouse, Authentication authentication) {
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
    public void deleteHouse(@PathVariable int id, Authentication authentication) {
        houseService.deleteHouseById(id);
    }

    @GetMapping("/{houseId}/current-occupant")
    public Integer getCurrentOccupant(@PathVariable Integer houseId) {
            return houseService.getCurrentOccupant(houseId);
    }


    @PostMapping("/{houseId}/current-occupant/increase")
    public Integer increaseOccupant(@PathVariable Integer houseId, Authentication authentication) {
        return houseService.incrementOccupant(houseId);
    }

    @PostMapping("/{houseId}/current-occupant/decrease")
    public Integer decreaseOccupant(@PathVariable Integer houseId, Authentication authentication) {

        return houseService.decrementOccupant(houseId);

    }

}
