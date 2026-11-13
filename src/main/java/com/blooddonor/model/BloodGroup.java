package com.blooddonor.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BloodGroup {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    private final String label;

    BloodGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BloodGroup fromLabel(String label) {
        for (BloodGroup g : values()) {
            if (g.label.equalsIgnoreCase(label) || g.name().equalsIgnoreCase(label)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid blood group: " + label);
    }

    public static List<String> allLabels() {
        return Arrays.stream(values()).map(BloodGroup::getLabel).collect(Collectors.toList());
    }
}
