package org.beaconfire.housing.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Employee") // use your actual MongoDB collection name
public class Employee {

    @Id
    private String id;

    private String userId;
    private String houseId;
    private String firstName;
    private String lastName;
    private String preferredName;
    private String cellPhone;
    private String email;
}