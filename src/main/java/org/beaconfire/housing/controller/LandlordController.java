package org.beaconfire.housing.controller;


import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.service.LandlordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/landlord")
public class LandlordController {

    @Autowired
    private LandlordService landlordService;

    @GetMapping
    public Page<Landlord> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String email
    ) {
        return landlordService.getAllLandlords(page, size, sortBy, sortDir, email);
    }

    @GetMapping("/{id}")
    public Landlord getById(@PathVariable Integer id) {
        return landlordService.getLandlordById(id);
    }

    @PostMapping
    public Landlord create(@RequestBody Landlord landlord) {
        try{
            return landlordService.createLandlord(landlord);
        }
        catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException(e.getMessage());
        }
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