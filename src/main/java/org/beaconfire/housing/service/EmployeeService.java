package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Employee;
import org.beaconfire.housing.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Integer getHouseIdByUserId(String userId) {
        return employeeRepository.findByUserId(userId)
                .map(Employee::getHouseId)
                .orElseThrow(() -> new IllegalArgumentException("No employee found for userId: " + userId));
    }

    public List<Employee> getUserIdsByHouseId(Integer houseId) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getHouseId() != null && emp.getHouseId().equals(houseId))
                .collect(Collectors.toList());
    }

    public void updateHouseId(String userId, Integer newHouseId) {
        employeeRepository.findByUserId(userId).ifPresent(employee -> {
            employee.setHouseId(newHouseId);
            employeeRepository.save(employee);
        });
    }
}
