package org.beaconfire.housing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Facility",
        indexes = {
                @Index(name = "idx_house_id", columnList = "HouseID"),
                @Index(name = "idx_type", columnList = "Type")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @NotNull(message = "House ID is required")
    @Column(name = "HouseID", nullable = false)
    private Integer houseId;

    @NotBlank(message = "Facility type is required")
    @Size(max = 100, message = "Type must not exceed 100 characters")
    @Column(name = "Type", nullable = false, length = 100)
    private String type;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "CreateDate",
            updatable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Column(name = "LastModificationDate",
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp lastModificationDate;

    // Relationship with House
    @ManyToOne
    @JoinColumn(name = "HouseID", nullable = false, insertable = false, updatable = false)
    private House house;

    // Relationship with FacilityReport
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FacilityReport> facilityReports = new ArrayList<>();
}
