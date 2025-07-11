package org.beaconfire.housing.controller;


import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/housing")
public class HousingController {
    @Autowired
    private HouseService houseService;

    @PostMapping
    public ResponseEntity<?> createHouse(@RequestBody House house, Authentication authentication) {
        boolean isHr = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
        if (!isHr) {
            return ResponseEntity.status(403).body("Only HR can create houses.");
        }

        houseService.createHouse(house);
        return ResponseEntity.ok("House created successfully.");
    }


}
