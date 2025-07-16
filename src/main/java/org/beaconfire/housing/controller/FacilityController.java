package org.beaconfire.housing.controller;

import org.beaconfire.housing.dto.request.FacilityRequest;
import org.beaconfire.housing.dto.PageListResponse;
import org.beaconfire.housing.entity.Facility;
import org.beaconfire.housing.entity.House;
import org.beaconfire.housing.exception.RoleCheckException;
import org.beaconfire.housing.service.FacilityService;
import org.beaconfire.housing.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/facilities")
public class FacilityController {
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private HouseService houseService;

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }


    @GetMapping
    public PageListResponse<Facility> getFacilities(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Integer houseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer quantity
    ) {
        if (!hasRole(authentication, "ROLE_HR")) {
            throw new RoleCheckException("Only HR can view all houses.");
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Facility> facilities = facilityService.getFacilities(houseId, type, quantity, pageable);

        return PageListResponse.<Facility>builder()
                .list(facilities.getContent())
                .current(facilities.getNumber() + 1)
                .pageSize(facilities.getSize())
                .total(facilities.getTotalElements())
                .build();
    }

    @GetMapping("/{id}")
    public Facility getFacilityById(@PathVariable int id) {
        Facility facility = facilityService.getFacilityById(id);
        if (facility != null) {
            return facility;
        } else {
            throw new NoSuchElementException();
        }
    }

    @PostMapping
    public Facility createFacility(@RequestBody FacilityRequest facilityRequest) {
        House house = houseService.getHouseById(facilityRequest.getHouseId());

        Facility facility = new Facility();
        facility.setType(facilityRequest.getType());
        facility.setQuantity(facilityRequest.getQuantity());
        if (facilityRequest.getDescription() != null) {
            facility.setDescription(facilityRequest.getDescription());
        }
        facility.setHouse(house);
        Facility createdFacility = facilityService.createFacility(facility);
        return createdFacility;
    }

    @PutMapping("/{id}")
    public Facility updateFacility(@PathVariable int id, @RequestBody FacilityRequest req) {

        House house = houseService.getHouseById(req.getHouseId());

        Facility facility = new Facility();
        facility.setType(req.getType());
        facility.setQuantity(req.getQuantity());
        if (req.getDescription() != null) {
            facility.setDescription(req.getDescription());
        }
        facility.setHouse(house);

        Facility updatedFacility = facilityService.updateFacility(id, facility);
        return updatedFacility;

    }

    @DeleteMapping("/{id}")
    public void deleteFacility(@PathVariable int id) {
        facilityService.deleteFacility(id);
    }

}
