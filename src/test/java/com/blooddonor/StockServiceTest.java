package com.blooddonor;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.BloodUnit;
import com.blooddonor.repository.BloodUnitRepository;
import com.blooddonor.service.AppSettings;
import com.blooddonor.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock BloodUnitRepository bloodUnitRepository;
    @Mock AppSettings settings;
    StockService stockService;

    @BeforeEach
    void setUp() {
        lenient().when(settings.getLowStockThreshold()).thenReturn(3);
        lenient().when(settings.getExpiryWarningDays()).thenReturn(7);
        stockService = new StockService(bloodUnitRepository, settings);
    }

    @Test void lowStockWhenBelowThreshold() {
        for (BloodGroup g : BloodGroup.values()) {
            when(bloodUnitRepository.countByBloodGroupAndInStockTrue(g)).thenReturn(g == BloodGroup.O_NEG ? 1L : 10L);
        }
        assertTrue(stockService.lowStockGroups().contains(BloodGroup.O_NEG));
    }

    @Test void expiringSoonUsesWarningWindow() {
        BloodUnit unit = new BloodUnit();
        unit.setBloodGroup(BloodGroup.A_POS);
        unit.setCollectionDate(LocalDate.now().minusDays(5));
        unit.setExpiryDate(LocalDate.now().plusDays(3));
        unit.setInStock(true);
        when(bloodUnitRepository.findExpiringOnOrBefore(any())).thenReturn(List.of(unit));
        assertEquals(1, stockService.expiringSoon().size());
    }

    @Test void addUnitRejectsBadDates() {
        assertThrows(IllegalArgumentException.class, () ->
                stockService.addUnit(BloodGroup.A_POS, LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)));
        assertThrows(IllegalArgumentException.class, () ->
                stockService.addUnit(BloodGroup.A_POS, LocalDate.now().minusDays(1), LocalDate.now().minusDays(2)));
    }
}
