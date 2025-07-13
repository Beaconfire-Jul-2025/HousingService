package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByUserId(String userId);
}