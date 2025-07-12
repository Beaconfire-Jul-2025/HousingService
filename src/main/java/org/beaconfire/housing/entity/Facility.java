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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Facility",
        indexes = {
                @Index(name = "idx_house_id", columnList = "HouseID"),
                @Index(name = "idx_type", columnList = "Type"),
                @Index(name = "idx_quantity", columnList = "Quantity")
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

    @Column(name = "CreateDate", updatable = false, nullable = false)
    private Timestamp createDate;

    @Column(name = "LastModificationDate", nullable = false)
    private Timestamp lastModificationDate;

    // Relationship with House
    @NotNull(message = "House ID is required")
    @ManyToOne
    @JoinColumn(name = "HouseID", nullable = false)
    private House house;

    // Relationship with FacilityReport
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FacilityReport> facilityReports = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
        this.lastModificationDate = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModificationDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
