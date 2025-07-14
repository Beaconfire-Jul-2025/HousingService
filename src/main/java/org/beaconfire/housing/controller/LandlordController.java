package org.beaconfire.housing.controller;


import org.beaconfire.housing.entity.Landlord;
import org.beaconfire.housing.exception.UserNotFoundException;
import org.beaconfire.housing.service.LandlordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/landlord")
public class LandlordController {

    @Autowired
    private LandlordService landlordService;

    @GetMapping("/all")
    public ResponseEntity<Page<Landlord>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String email
    ) {
        return ResponseEntity.ok(landlordService.getAllLandlords(page, size, sortBy, sortDir, email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return landlordService.getLandlordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Landlord> create(@RequestBody Landlord landlord) {
        try{
            return ResponseEntity.ok(landlordService.createLandlord(landlord));
        }
        catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Landlord> update(@PathVariable Integer id, @RequestBody Landlord landlord) {
        try {
            return ResponseEntity.ok(landlordService.updateLandlord(id, landlord));
        }
        catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().build();
        }
        catch (UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try{
            landlordService.deleteLandlord(id);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}