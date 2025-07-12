package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Integer> {
}
