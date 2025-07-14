package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer>, JpaSpecificationExecutor<Facility> {

    // Find facilities by house id and type (e.g. house id 1, type "Bedroom")
    public Optional<Facility> findByHouseIdAndType(Integer houseId, String type);

}
