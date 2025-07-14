package org.beaconfire.housing.service;

import org.beaconfire.housing.entity.Employee;
import org.beaconfire.housing.exception.UserNotFoundException;
import org.beaconfire.housing.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public String getHouseIdByUserId(String userId) {
        return employeeRepository.findByUserId(userId)
                .map(Employee::getHouseId)
                .orElseThrow(() -> new UserNotFoundException("No employee found for userId: " + userId.toString()));
    }

    public List<Employee> getUserIdsByHouseId(Integer houseId) {
        return employeeRepository.findAll().stream()
                .filter(emp -> emp.getHouseId() != null && emp.getHouseId().equals(houseId))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeByUserId(String userId) {
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("No employee found for userId: " + userId.toString()));
    }

    public void updateHouseId(String userId, String newHouseId) {
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("No employee found for userId: " + userId));
        employee.setHouseId(newHouseId);
        employeeRepository.save(employee);
    }
}
