package com.blooddonor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppSettings {

    @Value("${app.donation.min-gap-days:90}")
    private int minGapDays;

    @Value("${app.donor.min-age:18}")
    private int minAge;

    @Value("${app.donor.max-age:65}")
    private int maxAge;

    @Value("${app.stock.low-threshold:3}")
    private int lowStockThreshold;

    @Value("${app.stock.expiry-warning-days:7}")
    private int expiryWarningDays;

    public int getMinGapDays() { return minGapDays; }
    public int getMinAge() { return minAge; }
    public int getMaxAge() { return maxAge; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public int getExpiryWarningDays() { return expiryWarningDays; }
}
