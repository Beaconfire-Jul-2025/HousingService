package org.beaconfire.housing.controller;


import org.beaconfire.housing.dto.AssignRequest;
import org.beaconfire.housing.dto.HouseDTO;
import org.beaconfire.housing.entity.Employee;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.exception.UserNotFoundException;
import org.beaconfire.housing.messaging.HouseAssignProducer;
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
@RequestMapping("/house")
public class HousingController {

    @Autowired
    private HouseService houseService;

//    @Autowired
//    private EmployeeService employeeService;
//
//    @Autowired
//    private HouseAssignProducer houseAssignProducer;

    // Role Check
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }

//    @GetMapping
//    public ResponseEntity<?> getAssignedHouse(Authentication authentication) {
//        // role validation
//        boolean isHr = hasRole(authentication, "ROLE_HR");
//        boolean isEmployee = hasRole(authentication, "ROLE_EMPLOYEE");
//
//
//        if (!(isHr || isEmployee)) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Unauthorized access.");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//        }
//
//        String userId = authentication.getName();
//        Integer houseId;
//        List<Map<String, String>> tenantInfoList;
//        try{
//            // retrieve HouseId by UserId from mongodb
//            houseId = Integer.valueOf(employeeService.getHouseIdByUserId(userId));
//            if (houseId == null) {
//                Map<String, String> response = new HashMap<>();
//                response.put("message", "No house assigned.");
//                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
//            }
//
//            // retrieve users who have the same HouseID
//            List<Employee> tenants = employeeService.getUserIdsByHouseId(houseId);
//
//            tenantInfoList = tenants.stream().map(emp -> {
//                Map<String, String> info = new HashMap<>();
//                String name = (emp.getPreferredName() != null) ? emp.getPreferredName() : emp.getFirstName();
//                info.put("name", name);
//                info.put("lastName", emp.getLastName());
//                info.put("cellPhone", emp.getCellPhone() != null ? emp.getCellPhone() : "Not Available");
//                return info;
//            }).collect(Collectors.toList());
//        }
//        catch (UserNotFoundException e){
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        // retrieve addr about the house
//        String address;
//        try {
//            address = houseService.getHouseById(houseId).getAddress();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("houseId", houseId);
//        result.put("address", address);
//        result.put("tenants", tenantInfoList);
//        return ResponseEntity.ok(result);
//
//    }

//    @PostMapping("/assign")
//    public ResponseEntity<?> assignHouse(@RequestBody AssignRequest req, Authentication authentication) {
//        boolean isHr = authentication.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_HR"));
//        if (!isHr) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Only HR can assign housing.");
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//        }
//        String houseId = req.getHouseId();
//        String userId = req.getUserId();
//
//        // check if houseId exists
//        if (!houseService.houseExists(Integer.parseInt(houseId))) {
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "House with ID " + houseId + " does not exist.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        // update mongodb houseid
//        try{
//            employeeService.updateHouseId(userId, houseId);
//        }
//        catch (UserNotFoundException e){
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        // get user email and send to rabbitMQ
//        try{
//            Employee employee = employeeService.getEmployeeByUserId(userId);
//            houseAssignProducer.sendAssignmentMessage(employee.getEmail(), userId, houseId);
//        }
//        catch (UserNotFoundException e){
//            Map<String, String> response = new HashMap<>();
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "House assigned successfully.");
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//
//    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllHouses(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_HR")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Only HR can view all houses.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        List<House> houses = houseService.getAllHouses();
        return ResponseEntity.ok(houses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHouseById(@PathVariable int id, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_HR")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Only HR can view a house by ID.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        try {
            House house = houseService.getHouseById(id);
            return ResponseEntity.ok(house);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createHouse(@Valid @RequestBody HouseDTO dto, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_HR")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Only HR can create houses.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
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
