package com.blooddonor.service;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.Donor;
import com.blooddonor.repository.DonorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final AppSettings settings;

    public DonorService(DonorRepository donorRepository, AppSettings settings) {
        this.donorRepository = donorRepository;
        this.settings = settings;
    }

    public List<Donor> findAll() {
        return donorRepository.findAll();
    }

    public Optional<Donor> findById(Long id) {
        return donorRepository.findById(id);
    }

    @Transactional
    public Donor save(Donor donor) {
        validateDonor(donor);
        return donorRepository.save(donor);
    }

    @Transactional
    public void delete(Long id) {
        donorRepository.deleteById(id);
    }

    public boolean isEligible(Donor donor) {
        if (!donor.isAvailable()) {
            return false;
        }
        if (donor.getLastDonationDate() == null) {
            return true;
        }
        long days = ChronoUnit.DAYS.between(donor.getLastDonationDate(), LocalDate.now());
        return days >= settings.getMinGapDays();
    }

    public LocalDate nextEligibleDate(Donor donor) {
        if (donor.getLastDonationDate() == null) {
            return LocalDate.now();
        }
        return donor.getLastDonationDate().plusDays(settings.getMinGapDays());
    }

    public List<Donor> searchEligibleFirst(BloodGroup group, String city) {
        String cityParam = (city == null || city.isBlank()) ? null : city.trim();
        List<Donor> list = donorRepository.search(group, cityParam);
        list.sort(Comparator
                .comparing((Donor d) -> !isEligible(d))
                .thenComparing(Donor::getName));
        return list;
    }

    public long countAvailableFlag() {
        return donorRepository.countByAvailableTrue();
    }

    public long countEligible() {
        return donorRepository.findAll().stream().filter(this::isEligible).count();
    }

    private void validateDonor(Donor donor) {
        if (donor.getAge() < settings.getMinAge() || donor.getAge() > settings.getMaxAge()) {
            throw new IllegalArgumentException(
                    "Age must be between " + settings.getMinAge() + " and " + settings.getMaxAge()
                            + " (configurable demo rule).");
        }
        if (donor.getLastDonationDate() != null && donor.getLastDonationDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Last donation date cannot be in the future.");
        }
        if (donor.getBloodGroup() == null) {
            throw new IllegalArgumentException("Blood group is required.");
        }
    }
}
