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
//    public ResponseEntity<?> getAssignedHouse(@RequestBody int id, Authentication authentication) {
//        // role validation
//
//        // mongodb
//        // retrive HouseID from mongodb
//        // retrive userIDs who have the same HouseID
//        // retrive their names and phone
//
//        // mysql
//        // retrive addr about the house
//        // retrive open facility report
//
//
//    }

    @PostMapping
    public ResponseEntity<?> createHouse(@Valid @RequestBody HouseDTO dto, Authentication authentication) {
        boolean isHr = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
        if (!isHr) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Only HR can create houses.");
            return ResponseEntity.status(403).body(response);
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
  
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHouse(@PathVariable int id, @RequestBody HouseDTO updatedHouse, Authentication authentication) {
        boolean isHr = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
        if (!isHr) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Only HR can update houses.");
            return ResponseEntity.status(403).body(response);
        }

        try {
            House house = new House();
            house.setId(id);
            house.setLandlordId(updatedHouse.getLandlordId());
            house.setAddress(updatedHouse.getAddress());
            house.setMaxOccupant(updatedHouse.getMaxOccupant());
            house.setDescription(updatedHouse.getDescription());
            houseService.updateHouse(house);
            Map<String, String> response = new HashMap<>();
            response.put("message", "House updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHouse(@PathVariable int id, Authentication authentication) {
        boolean isHr = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
        if (!isHr) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Only HR can delete houses.");
            return ResponseEntity.status(403).body(response);
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
