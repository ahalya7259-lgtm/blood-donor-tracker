package com.blooddonor.service;

import com.blooddonor.model.BloodGroup;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CompatibilityService {

    private final Map<BloodGroup, List<BloodGroup>> canDonateTo = new EnumMap<>(BloodGroup.class);

    public CompatibilityService() {
        canDonateTo.put(BloodGroup.O_NEG, List.of(
                BloodGroup.O_NEG, BloodGroup.O_POS,
                BloodGroup.A_NEG, BloodGroup.A_POS,
                BloodGroup.B_NEG, BloodGroup.B_POS,
                BloodGroup.AB_NEG, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.O_POS, List.of(
                BloodGroup.O_POS, BloodGroup.A_POS, BloodGroup.B_POS, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.A_NEG, List.of(
                BloodGroup.A_NEG, BloodGroup.A_POS, BloodGroup.AB_NEG, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.A_POS, List.of(BloodGroup.A_POS, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.B_NEG, List.of(
                BloodGroup.B_NEG, BloodGroup.B_POS, BloodGroup.AB_NEG, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.B_POS, List.of(BloodGroup.B_POS, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.AB_NEG, List.of(BloodGroup.AB_NEG, BloodGroup.AB_POS));
        canDonateTo.put(BloodGroup.AB_POS, List.of(BloodGroup.AB_POS));
    }

    public List<BloodGroup> recipientsFor(BloodGroup donorGroup) {
        return canDonateTo.getOrDefault(donorGroup, List.of());
    }

    public Map<BloodGroup, List<BloodGroup>> allRules() {
        return Map.copyOf(canDonateTo);
    }
}
