package org.beaconfire.housing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "FacilityReportDetail", indexes = {
        @Index(name = "idx_facility_report_id", columnList = "FacilityReportID"),
        @Index(name = "idx_employee_id", columnList = "EmployeeID"),
        @Index(name = "idx_create_date", columnList = "CreateDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityReportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    // Relation with FacilityReport
    @NotNull(message = "Facility Report ID is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FacilityReportID", nullable = false)
    private FacilityReport facilityReport;

    // Relation with Employee
    @NotNull(message = "Employee ID is required")
    @Size(max = 100, message = "Employee ID must not exceed 100 characters")
    @Column(name = "EmployeeID", nullable = false, length = 100)
    private String employeeId;

    @NotBlank(message = "Comment is required")
    @Column(name = "Comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "CreateDate", updatable = false, nullable = false)
    private Timestamp createDate;

    @Column(name = "LastModificationDate", nullable = false)
    private Timestamp lastModificationDate;

    @PrePersist
    public void prePersist() {
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModificationDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
