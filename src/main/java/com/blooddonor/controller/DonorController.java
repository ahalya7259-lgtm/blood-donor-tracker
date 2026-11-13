package com.blooddonor.controller;

import com.blooddonor.model.BloodGroup;
import com.blooddonor.model.Donor;
import com.blooddonor.service.CsvExportService;
import com.blooddonor.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/donors")
public class DonorController {

    private final DonorService donorService;
    private final CsvExportService csvExportService;

    public DonorController(DonorService donorService, CsvExportService csvExportService) {
        this.donorService = donorService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String bloodGroup,
                       @RequestParam(required = false) String city,
                       Model model) {
        BloodGroup group = null;
        if (bloodGroup != null && !bloodGroup.isBlank()) {
            try {
                group = BloodGroup.fromLabel(bloodGroup);
            } catch (IllegalArgumentException ignored) {
            }
        }
        var donors = donorService.searchEligibleFirst(group, city);
        model.addAttribute("donors", donors);
        model.addAttribute("bloodGroups", BloodGroup.allLabels());
        model.addAttribute("selectedGroup", bloodGroup);
        model.addAttribute("selectedCity", city);
        model.addAttribute("donorService", donorService);
        return "donors/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("donor", new Donor());
        model.addAttribute("bloodGroups", BloodGroup.values());
        return "donors/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Donor donor = donorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));
        model.addAttribute("donor", donor);
        model.addAttribute("bloodGroups", BloodGroup.values());
        return "donors/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Donor donor, BindingResult bindingResult,
                       Model model, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "donors/form";
        }
        try {
            donor.setSampleData(true);
            donorService.save(donor);
            ra.addFlashAttribute("message", "Donor saved (SAMPLE DATA).");
            return "redirect:/donors";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "donors/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        donorService.delete(id);
        ra.addFlashAttribute("message", "Donor deleted.");
        return "redirect:/donors";
    }

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        String csv = csvExportService.donorsToCsv(donorService.findAll(), donorService);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=donors-sample.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
