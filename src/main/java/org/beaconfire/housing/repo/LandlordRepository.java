package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.Landlord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Integer> {
    Page<Landlord> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}