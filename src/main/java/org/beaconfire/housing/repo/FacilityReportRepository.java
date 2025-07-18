package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.FacilityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityReportRepository extends JpaRepository<FacilityReport, Integer>, JpaSpecificationExecutor<FacilityReport> {

    // For GET /housing/facility-reports?houseId=1
    @Query("SELECT fr FROM FacilityReport fr " +
            "JOIN fr.facility f " +
            "WHERE f.house.id = :houseId ")
    List<FacilityReport> findByHouseId(Integer houseId);
}
