package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer>, JpaSpecificationExecutor<Facility> {

    // Find facilities by house id and type (e.g. house id 1, type "Bedroom")
    public Optional<Facility> findByHouseIdAndType(Integer houseId, String type);

    @Query("SELECT f FROM Facility f WHERE " +
            "(:type IS NULL OR UPPER(f.type) LIKE UPPER(CONCAT('%', :type, '%'))) AND " +
            "(:houseId IS NULL OR f.house.id = :houseId) AND " +
            "(:quantity IS NULL OR f.quantity = :quantity)")
    Page<Facility> findByFilters(@Param("houseId") Integer houseId,
                                 @Param("type") String type,
                                 @Param("quantity") Integer quantity,
                                 Pageable pageable);
}
