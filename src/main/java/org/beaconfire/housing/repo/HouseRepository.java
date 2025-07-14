package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
    Page<House> findByAddressContainingIgnoreCase(String address, Pageable pageable);
}
