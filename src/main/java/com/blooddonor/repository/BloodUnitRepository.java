package com.blooddonor.repository;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.BloodUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {

    List<BloodUnit> findByInStockTrue();

    long countByBloodGroupAndInStockTrue(BloodGroup bloodGroup);

    @Query("SELECT u FROM BloodUnit u WHERE u.inStock = true AND u.expiryDate <= :limit")
    List<BloodUnit> findExpiringOnOrBefore(LocalDate limit);

    List<BloodUnit> findByBloodGroupAndInStockTrue(BloodGroup bloodGroup);
}
