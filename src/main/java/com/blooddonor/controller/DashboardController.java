package com.blooddonor.controller;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.service.CompatibilityService;
import com.blooddonor.service.DonorService;
import com.blooddonor.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final DonorService donorService;
    private final StockService stockService;
    private final CompatibilityService compatibilityService;

    public DashboardController(DonorService donorService, StockService stockService,
                               CompatibilityService compatibilityService) {
        this.donorService = donorService;
        this.stockService = stockService;
        this.compatibilityService = compatibilityService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        Map<BloodGroup, Long> stock = stockService.stockByGroup();
        model.addAttribute("stockLabels", stock.keySet().stream().map(BloodGroup::getLabel).collect(Collectors.toList()));
        model.addAttribute("stockValues", stock.values());
        model.addAttribute("eligibleCount", donorService.countEligible());
        model.addAttribute("donorCount", donorService.findAll().size());
        model.addAttribute("lowStock", stockService.lowStockGroups());
        model.addAttribute("expiring", stockService.expiringSoon());
        model.addAttribute("compat", compatibilityService.allRules());
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/compatibility")
    public String compatibility(Model model) {
        Map<String, String> display = new LinkedHashMap<>();
        compatibilityService.allRules().forEach((k, v) ->
                display.put(k.getLabel(), v.stream().map(BloodGroup::getLabel).collect(Collectors.joining(", "))));
        model.addAttribute("rules", display);
        return "compatibility";
    }
}
