package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface HouseRepository extends JpaRepository<House, Integer> {
}
