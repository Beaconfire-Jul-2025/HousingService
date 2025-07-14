package org.beaconfire.housing.controller;

import org.beaconfire.housing.dto.FacilityRequest;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.service.FacilityService;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/facilities")
public class FacilityController {
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private HouseService houseService;


    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<Facility>> getFacilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer houseId
    ) {
        Page<Facility> facilities = facilityService.getAllFacilities(page, size, sortBy, sortDir, type, houseId);
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facility> getFacilityById(@PathVariable int id) {
        Facility facility = facilityService.getFacilityById(id);
        if (facility != null) {
            return ResponseEntity.ok(facility);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createFacility(@RequestBody FacilityRequest facilityRequest) {
        try {
            House house = houseService.getHouseById(facilityRequest.getHouseId());

            Facility facility = new Facility();
            facility.setType(facilityRequest.getType());
            facility.setQuantity(facilityRequest.getQuantity());
            if (facilityRequest.getDescription() != null) {
                facility.setDescription(facilityRequest.getDescription());
            }
            facility.setHouse(house);
            Facility createdFacility = facilityService.createFacility(facility);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFacility);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFacility(@PathVariable int id, @RequestBody FacilityRequest req) {
        try {

            House house = houseService.getHouseById(req.getHouseId());
            Facility facility = new Facility();
            facility.setType(req.getType());
            facility.setQuantity(req.getQuantity());
            if (req.getDescription() != null) {
                facility.setDescription(req.getDescription());
            }
            facility.setHouse(house);

            Facility updatedFacility = facilityService.updateFacility(id, facility);
            return ResponseEntity.ok(updatedFacility);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFacility(@PathVariable int id) {
        try {
            facilityService.deleteFacility(id);
            return ResponseEntity.ok("Facility deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }





}
