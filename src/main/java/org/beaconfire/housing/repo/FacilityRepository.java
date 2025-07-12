package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Integer> {

    // Find all facilities for a specific house
    public List<Facility> findByHouseId(int houseId);

}
