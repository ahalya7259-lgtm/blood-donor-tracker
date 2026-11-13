package com.blooddonor.service;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.BloodUnit;
import com.blooddonor.repository.BloodUnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class StockService {

    private final BloodUnitRepository bloodUnitRepository;
    private final AppSettings settings;

    public StockService(BloodUnitRepository bloodUnitRepository, AppSettings settings) {
        this.bloodUnitRepository = bloodUnitRepository;
        this.settings = settings;
    }

    public List<BloodUnit> findInStock() {
        return bloodUnitRepository.findByInStockTrue();
    }

    public List<BloodUnit> findAll() {
        return bloodUnitRepository.findAll();
    }

    @Transactional
    public BloodUnit addUnit(BloodGroup group, LocalDate collection, LocalDate expiry) {
        if (collection == null || expiry == null) {
            throw new IllegalArgumentException("Collection and expiry dates are required.");
        }
        if (collection.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Collection date cannot be in the future.");
        }
        if (!expiry.isAfter(collection)) {
            throw new IllegalArgumentException("Expiry must be after collection date.");
        }
        BloodUnit unit = new BloodUnit();
        unit.setBloodGroup(group);
        unit.setCollectionDate(collection);
        unit.setExpiryDate(expiry);
        unit.setInStock(true);
        unit.setSampleData(true);
        return bloodUnitRepository.save(unit);
    }

    @Transactional
    public void removeUnit(Long id) {
        bloodUnitRepository.findById(id).ifPresent(u -> {
            u.setInStock(false);
            bloodUnitRepository.save(u);
        });
    }

    public Map<BloodGroup, Long> stockByGroup() {
        Map<BloodGroup, Long> map = new EnumMap<>(BloodGroup.class);
        for (BloodGroup g : BloodGroup.values()) {
            map.put(g, bloodUnitRepository.countByBloodGroupAndInStockTrue(g));
        }
        return map;
    }

    public List<BloodGroup> lowStockGroups() {
        List<BloodGroup> low = new ArrayList<>();
        for (BloodGroup g : BloodGroup.values()) {
            long count = bloodUnitRepository.countByBloodGroupAndInStockTrue(g);
            if (count < settings.getLowStockThreshold()) {
                low.add(g);
            }
        }
        return low;
    }

    public List<BloodUnit> expiringSoon() {
        LocalDate limit = LocalDate.now().plusDays(settings.getExpiryWarningDays());
        return bloodUnitRepository.findExpiringOnOrBefore(limit).stream()
                .filter(BloodUnit::isInStock)
                .toList();
    }
}
