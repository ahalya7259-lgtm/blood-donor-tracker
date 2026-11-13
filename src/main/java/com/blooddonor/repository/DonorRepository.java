package com.blooddonor.repository;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    List<Donor> findByBloodGroup(BloodGroup bloodGroup);

    List<Donor> findByCityIgnoreCase(String city);

    @Query("""
        SELECT d FROM Donor d
        WHERE (:group IS NULL OR d.bloodGroup = :group)
          AND (:city IS NULL OR LOWER(d.city) = LOWER(:city))
        ORDER BY d.name
        """)
    List<Donor> search(@Param("group") BloodGroup group, @Param("city") String city);

    long countByAvailableTrue();
}
