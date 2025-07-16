package org.beaconfire.housing.repo;

import org.beaconfire.housing.entity.Landlord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Integer> {

    @Query("SELECT l FROM Landlord l WHERE " +
            "(:firstName IS NULL OR UPPER(l.firstName) LIKE UPPER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR l.lastName LIKE UPPER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR l.email = :email) AND " +
            "(:cellPhone IS NULL OR l.cellPhone = :cellPhone)")
    Page<Landlord> findByFilters(@Param("firstName") String firstName,
                                 @Param("lastName") String lastName,
                                 @Param("email") String email,
                                 @Param("cellPhone") String cellPhone,
                                 Pageable pageable);
}