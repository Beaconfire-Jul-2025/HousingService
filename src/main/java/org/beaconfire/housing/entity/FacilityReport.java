package org.beaconfire.housing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "FacilityReport", indexes = {
        @Index(name = "idx_facility_id", columnList = "FacilityID"),
        @Index(name = "idx_employee_id", columnList = "EmployeeID"),
        @Index(name = "idx_title", columnList = "Title"),
        @Index(name = "idx_create_date", columnList = "CreateDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FacilityID", nullable = false)
    private Facility facility;

    @Column(name = "EmployeeID", nullable = false, length = 100) // to match var(100) in database
    private Integer employeeId;

    @Column(name = "Title", nullable = false, length = 255) // to match var(255) in database
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "CreateDate", updatable = false, nullable = false)
    private Timestamp createDate;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @PrePersist
    public void prePersist() {
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
