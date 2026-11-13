package com.blooddonor;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.Donor;
import com.blooddonor.repository.DonorRepository;
import com.blooddonor.service.AppSettings;
import com.blooddonor.service.DonorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonorServiceTest {

    @Mock DonorRepository donorRepository;
    @Mock AppSettings settings;
    DonorService donorService;

    @BeforeEach
    void setUp() {
        lenient().when(settings.getMinGapDays()).thenReturn(90);
        lenient().when(settings.getMinAge()).thenReturn(18);
        lenient().when(settings.getMaxAge()).thenReturn(65);
        donorService = new DonorService(donorRepository, settings);
    }

    private Donor donor(boolean available, LocalDate lastDonation) {
        Donor d = new Donor();
        d.setName("Sample Person");
        d.setAge(30);
        d.setBloodGroup(BloodGroup.O_POS);
        d.setPhone("90000 00001");
        d.setCity("Mysuru");
        d.setAvailable(available);
        d.setLastDonationDate(lastDonation);
        d.setSampleData(true);
        return d;
    }

    @Test void eligibleWhenNoPriorDonation() { assertTrue(donorService.isEligible(donor(true, null))); }
    @Test void notEligibleWhenUnavailable() { assertFalse(donorService.isEligible(donor(false, null))); }
    @Test void eligibleAfterGap() { assertTrue(donorService.isEligible(donor(true, LocalDate.now().minusDays(100)))); }
    @Test void notEligibleWithinGap() { assertFalse(donorService.isEligible(donor(true, LocalDate.now().minusDays(30)))); }
    @Test void nextEligibleDateAddsGap() {
        assertEquals(LocalDate.of(2026, 4, 1), donorService.nextEligibleDate(donor(true, LocalDate.of(2026, 1, 1))));
    }
    @Test void searchPutsEligibleFirst() {
        Donor eligible = donor(true, LocalDate.now().minusDays(120)); eligible.setName("Eligible");
        Donor waiting = donor(true, LocalDate.now().minusDays(10)); waiting.setName("Waiting");
        when(donorRepository.search(BloodGroup.O_POS, null)).thenReturn(new ArrayList<>(List.of(waiting, eligible)));
        assertEquals("Eligible", donorService.searchEligibleFirst(BloodGroup.O_POS, null).get(0).getName());
    }
    @Test void rejectsFutureDonationDate() {
        assertThrows(IllegalArgumentException.class, () -> donorService.save(donor(true, LocalDate.now().plusDays(2))));
    }
    @Test void rejectsAgeOutsideRange() {
        Donor d = donor(true, null); d.setAge(17);
        assertThrows(IllegalArgumentException.class, () -> donorService.save(d));
    }
}
