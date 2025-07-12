package org.beaconfire.housing.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "House")
@Data
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "LandlordID", nullable = false)
    private Integer landlordId;

    @Column(name = "Address", length = 500)
    private String address;

    @Column(name = "MaxOccupant")
    private Integer maxOccupant;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "CreateDate", updatable = false)
    private Timestamp createDate;

    @Column(name = "LastModificationDate")
    private Timestamp lastModificationDate;
}