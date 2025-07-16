package org.beaconfire.housing.controller;


import org.beaconfire.housing.dto.PageListResponse;
import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.service.LandlordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('HR') or hasRole('COMPOSITE')")
@RequestMapping("/landlord")
public class LandlordController {

    @Autowired
    private LandlordService landlordService;

    @GetMapping
    public PageListResponse<Landlord> getAllLandlords(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cellPhone
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Landlord> landlords = landlordService.getAllLandlords(firstName, lastName, email, cellPhone, pageable);

        return PageListResponse.<Landlord>builder()
                .list(landlords.getContent())
                .current(landlords.getNumber() + 1)
                .pageSize(landlords.getSize())
                .total(landlords.getTotalElements())
                .build();
    }

    @GetMapping("/{id}")
    public Landlord getById(@PathVariable Integer id) {
        return landlordService.getLandlordById(id);
    }

    @PostMapping
    public Landlord create(@RequestBody Landlord landlord) {
        return landlordService.createLandlord(landlord);

    }

    @PutMapping("/{id}")
    public Landlord update(@PathVariable Integer id, @RequestBody Landlord landlord) {
        return landlordService.updateLandlord(id, landlord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        landlordService.deleteLandlord(id);
    }
}