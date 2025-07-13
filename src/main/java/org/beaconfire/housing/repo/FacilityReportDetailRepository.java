package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.FacilityReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityReportDetailRepository extends JpaRepository<FacilityReportDetail, Integer> {

    // Get all comments for a report
    List<FacilityReportDetail> findByIdOrderByCreateDateDesc(Integer reportId);
}
