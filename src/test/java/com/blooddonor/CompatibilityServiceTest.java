package com.blooddonor;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.service.CompatibilityService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompatibilityServiceTest {

    private final CompatibilityService service = new CompatibilityService();

    @Test
    void oNegIsUniversalDonorInMap() {
        assertEquals(8, service.recipientsFor(BloodGroup.O_NEG).size());
    }

    @Test
    void abPosReceivesOnlyFromAbPosAsDonorTargetSelf() {
        assertTrue(service.recipientsFor(BloodGroup.AB_POS).contains(BloodGroup.AB_POS));
        assertEquals(1, service.recipientsFor(BloodGroup.AB_POS).size());
    }
}
