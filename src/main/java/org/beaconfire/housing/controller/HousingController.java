package org.beaconfire.housing.controller;


import org.beaconfire.housing.dto.HouseDTO;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/housing")
public class HousingController {
    @Autowired
    private HouseService houseService;

//    @GetMapping
//    public ResponseEntity<?> getHouseById(@RequestBody int id, Authentication authentication) {
//
//    }

    @PostMapping
    public ResponseEntity<?> createHouse(@Valid @RequestBody HouseDTO dto, Authentication authentication) {
        boolean isHr = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
        if (!isHr) {
            return ResponseEntity.status(403).body("Only HR can create houses.");
        }

        House house = new House();
        house.setLandlordId(dto.getLandlordId());
        house.setAddress(dto.getAddress());
        house.setMaxOccupant(dto.getMaxOccupant());
        house.setDescription(dto.getDescription());

        try {
            houseService.createHouse(house);
            Map<String, String> response = new HashMap<>();
            response.put("message", "House created successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // landlord does not exist
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            // optional: if dont want duplicates, make the addr unique in db
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable int id, Authentication authentication) {
        boolean isHr = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
        if (!isHr) {
            return ResponseEntity.status(403).body("Only HR can delete houses.");
        }

        try {
            houseService.deleteHouseById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "House deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}