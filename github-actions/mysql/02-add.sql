USE HousingService;

INSERT INTO Landlord (firstName, lastName, email, cellPhone) VALUES
                                                                 ('Alice', 'Smith', 'alice.smith@example.com', '555-123-4567'),
                                                                 ('Bob', 'Johnson', 'bob.johnson@example.com', '555-987-6543'),
                                                                 ('Charlie', 'Brown', 'charlie.brown@example.com', '555-555-1212');

INSERT INTO House (landlordId, address, maxOccupant, description) VALUES
                                                                      (1, '123 Main St, Anytown, CA 90210', 4, 'Spacious 3-bedroom house with a large backyard.'),
                                                                      (1, '456 Oak Ave, Anytown, CA 90210', 2, 'Cozy 1-bedroom apartment near downtown.'),
                                                                      (2, '789 Pine Ln, Otherville, NY 10001', 5, 'Large family home with a two-car garage.'),
                                                                      (3, '101 Elm St, Villageton, TX 75001', 3, 'Modern townhouse with shared amenities.');

INSERT INTO Facility (houseId, type, quantity, description) VALUES
                                                                (1, 'Dishwasher', 1, 'Brand new stainless steel dishwasher.'),
                                                                (1, 'Washer/Dryer', 1, 'In-unit washer and dryer.'),
                                                                (2, 'Refrigerator', 1, 'Compact refrigerator.'),
                                                                (3, 'Oven', 1, 'Double oven, great for baking.'),
                                                                (3, 'Central AC', 1, 'High-efficiency central air conditioning.'),
                                                                (4, 'Gym Access', 1, 'Access to community gym.'),
                                                                (4, 'Swimming Pool', 1, 'Outdoor swimming pool.');

INSERT INTO FacilityReport (facilityId, employeeId, title, description) VALUES
                                                                            (1, '3', 'Dishwasher not draining', 'Dishwasher fills with water but does not drain properly after cycle.'),
                                                                            (2, '4', 'Dryer making loud noise', 'Dryer unit emits a loud screeching sound during operation.'),
                                                                            (3, '5', 'Refrigerator not cooling', 'Food spoilage due to insufficient cooling in the refrigerator.'),
                                                                            (5, '6', 'AC filter replacement needed', 'Reduced airflow and unusual odor from AC vents.');

INSERT INTO FacilityReportDetail (facilityReportId, employeeId, comment) VALUES
                                                                             (1, '4', 'Checked drain hose, no visible kinks.'),
                                                                             (1, '1', 'Scheduled technician visit for further diagnosis.'),
                                                                             (2, '5', 'Inspected dryer drum and belt, unable to identify source of noise.'),
                                                                             (3, '6', 'Verified power supply, seems to be an internal compressor issue.'),
                                                                             (4, '1', 'Ordered new AC filters. Will replace next week.');
