package com.blooddonor.service;

import com.blooddonor.model.BloodUnit;
import com.blooddonor.model.Donor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvExportService {

    public String donorsToCsv(List<Donor> donors, DonorService donorService) {
        String header = "id,name,age,blood_group,phone,city,last_donation,available,eligible,sample_data\n";
        String body = donors.stream().map(d -> String.join(",",
                String.valueOf(d.getId()),
                csv(d.getName()),
                String.valueOf(d.getAge()),
                d.getBloodGroup().getLabel(),
                csv(d.getPhone()),
                csv(d.getCity()),
                d.getLastDonationDate() == null ? "" : d.getLastDonationDate().toString(),
                String.valueOf(d.isAvailable()),
                String.valueOf(donorService.isEligible(d)),
                String.valueOf(d.isSampleData())
        )).collect(Collectors.joining("\n"));
        return header + body + "\n";
    }

    public String stockToCsv(List<BloodUnit> units) {
        String header = "id,blood_group,collection_date,expiry_date,in_stock,sample_data\n";
        String body = units.stream().map(u -> String.join(",",
                String.valueOf(u.getId()),
                u.getBloodGroup().getLabel(),
                u.getCollectionDate().toString(),
                u.getExpiryDate().toString(),
                String.valueOf(u.isInStock()),
                String.valueOf(u.isSampleData())
        )).collect(Collectors.joining("\n"));
        return header + body + "\n";
    }

    private static String csv(String v) {
        if (v == null) return "";
        String s = v.replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s + "\"";
        }
        return s;
    }
}
