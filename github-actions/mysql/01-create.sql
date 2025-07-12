CREATE DATABASE IF NOT EXISTS HousingService;
USE HousingService;

-- Landlord table
CREATE TABLE Landlord (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          firstName VARCHAR(100) NOT NULL,
                          lastName VARCHAR(100) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          cellPhone VARCHAR(20),
                          createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          lastModificationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          INDEX idx_name (firstName, lastName),
                          INDEX idx_email (email),
                          INDEX idx_phone (cellPhone)
);

-- House table
CREATE TABLE House (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       landlordId INT NOT NULL,
                       address VARCHAR(500) NOT NULL,
                       maxOccupant INT NOT NULL DEFAULT 1,
                       description TEXT,
                       createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       lastModificationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       FOREIGN KEY (landlordId) REFERENCES Landlord(id) ON DELETE CASCADE,
                       INDEX idx_landlord_id (landlordId),
                       INDEX idx_address (address(100)),
                       INDEX idx_max_occupant (maxOccupant)
);

-- Facility table
CREATE TABLE Facility (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          houseId INT NOT NULL,
                          type VARCHAR(100) NOT NULL,
                          quantity INT NOT NULL DEFAULT 1,
                          description TEXT,
                          createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          lastModificationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          FOREIGN KEY (houseId) REFERENCES House(id) ON DELETE CASCADE,
                          INDEX idx_house_id (houseId),
                          INDEX idx_type (type),
                          INDEX idx_quantity (quantity)
);

-- FacilityReport table
CREATE TABLE FacilityReport (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                facilityId INT NOT NULL,
                                employeeId VARCHAR(100) NOT NULL,
                                title VARCHAR(255) NOT NULL,
                                description TEXT,
                                createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                FOREIGN KEY (facilityId) REFERENCES Facility(id) ON DELETE CASCADE,
                                INDEX idx_facility_id (facilityId),
                                INDEX idx_employee_id (employeeId),
                                INDEX idx_title (title),
                                INDEX idx_create_date (createDate)
);

-- FacilityReportDetail table
CREATE TABLE FacilityReportDetail (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      facilityReportId INT NOT NULL,
                                      employeeId VARCHAR(100) NOT NULL,
                                      comment TEXT NOT NULL,
                                      createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                      FOREIGN KEY (facilityReportId) REFERENCES FacilityReport(id) ON DELETE CASCADE,
                                      INDEX idx_facility_report_id (facilityReportId),
                                      INDEX idx_employee_id (employeeId),
                                      INDEX idx_create_date (createDate)
);

-- Add check constraints
ALTER TABLE House
    ADD CONSTRAINT chk_max_occupant CHECK (maxOccupant > 0);

ALTER TABLE Facility
    ADD CONSTRAINT chk_quantity CHECK (quantity > 0);
