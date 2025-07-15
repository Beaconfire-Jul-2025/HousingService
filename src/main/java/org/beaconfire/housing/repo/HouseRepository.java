package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {

    @Query("SELECT h FROM House h WHERE " +
            "(:address IS NULL OR UPPER(h.address) LIKE UPPER(CONCAT('%', :address, '%'))) AND " +
            "(:maxOccupant IS NULL OR h.maxOccupant = :maxOccupant) AND " +
            "(:currentOccupant IS NULL OR h.currentOccupant = :currentOccupant) AND " +
            "(:landlordId IS NULL OR h.landlordId = :landlordId)")
    Page<House> findByFilters(@Param("address") String address,
                              @Param("maxOccupant") Integer maxOccupant,
                              @Param("currentOccupant") Integer currentOccupant,
                              @Param("landlordId") Integer landlordId,
                              Pageable pageable);
}
