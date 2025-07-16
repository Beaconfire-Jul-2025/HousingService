package org.beaconfire.housing.controller;


import org.beaconfire.housing.dto.HouseDTO;
import org.beaconfire.housing.dto.PageListResponse;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.exception.RoleCheckException;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    // Role Check
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }

//    @GetMapping("/assign")
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
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can view all houses.");
        }

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
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can view all houses.");
        }

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
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can view all houses.");
        }
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
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can view all houses.");
        }
        houseService.deleteHouseById(id);
    }

    @GetMapping("/{houseId}/current-occupant")
    public Integer getCurrentOccupant(@PathVariable Integer houseId) {
            return houseService.getCurrentOccupant(houseId);
    }


    @PostMapping("/{houseId}/current-occupant/increase")
    public Integer increaseOccupant(@PathVariable Integer houseId, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can change # of Occupants.");
        }
        return houseService.incrementOccupant(houseId);

    }

    @PostMapping("/{houseId}/current-occupant/decrease")
    public Integer decreaseOccupant(@PathVariable Integer houseId, Authentication authentication) {
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can change # of Occupants.");
        }
        return houseService.decrementOccupant(houseId);

    }

}
