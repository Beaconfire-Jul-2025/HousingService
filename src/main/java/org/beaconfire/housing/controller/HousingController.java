package org.beaconfire.housing.controller;


import org.beaconfire.housing.dto.HouseDTO;
import org.beaconfire.housing.entity.Employee;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.service.EmployeeService;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/housing")
public class HousingController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private EmployeeService employeeService;

    // Role Check
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }

    @GetMapping
    public ResponseEntity<?> getAssignedHouse(Authentication authentication) {
        // role validation
        boolean isHr = hasRole(authentication, "ROLE_HR");
        boolean isEmployee = hasRole(authentication, "ROLE_EMPLOYEE");


        if (!(isHr || isEmployee)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Unauthorized access.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String userId = authentication.getName();
        Integer houseId;
        List<Map<String, String>> tenantInfoList;
        try{
            // retrieve HouseId by UserId from mongodb
            houseId = employeeService.getHouseIdByUserId(userId);
            if (houseId == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No house assigned.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }

            // retrieve users who have the same HouseID
            List<Employee> tenants = employeeService.getUserIdsByHouseId(houseId);

            tenantInfoList = tenants.stream().map(emp -> {
                Map<String, String> info = new HashMap<>();
                String name = (emp.getPreferredName() != null) ? emp.getPreferredName() : emp.getFirstName();
                info.put("name", name);
                info.put("lastName", emp.getLastName());
                info.put("cellPhone", emp.getCellPhone() != null ? emp.getCellPhone() : "Not Available");
                return info;
            }).collect(Collectors.toList());
        }
        catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // retrieve addr about the house
        String address;
        try {
            address = houseService.getAddressById(houseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House not found for ID: " + houseId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("houseId", houseId);
        result.put("address", address);
        result.put("tenants", tenantInfoList);
        return ResponseEntity.ok(result);

    }

    @PostMapping
    public ResponseEntity<?> createHouse(@Valid @RequestBody HouseDTO dto, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_HR")) {
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
        if (!hasRole(authentication, "ROLE_HR")) {
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
        if (!hasRole(authentication, "ROLE_HR")) {
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
